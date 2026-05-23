package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.dtos.UpdateUserRolesDto;
import publication_quality_system.dtos.UserRoleDto;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.RoleErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.mapper.RoleMapper;
import publication_quality_system.repositories.PermissionRepository;
import publication_quality_system.repositories.RoleRepository;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.CognitoGroupService;
import publication_quality_system.services.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String ADMIN_ROLE = "ADMIN";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CognitoGroupService cognitoGroupService;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleDto> getUserRoles(Long userId) {
        User user = getUserOrThrow(userId);
        return roleMapper.toUserRoleDtoList(user.getRoles().stream().toList());
    }

    @Override
    @Transactional
    public List<UserRoleDto> assignRoleToUser(Long userId, Long roleId) {
        User user = getUserOrThrow(userId);
        Role role = getRoleOrThrow(roleId);

        cognitoGroupService.ensureGroupExists(role.getName());
        if (user.getRoles().add(role)) {
            userRepository.save(user);
        }
        cognitoGroupService.addUserToGroup(user.getUsername(), role.getName());
        return getUserRoles(userId);
    }

    @Override
    @Transactional
    public List<UserRoleDto> removeRoleFromUser(Long userId, Long roleId) {
        User user = getUserOrThrow(userId);
        Role role = getRoleOrThrow(roleId);

        user.getRoles().remove(role);
        userRepository.save(user);
        cognitoGroupService.removeUserFromGroup(user.getUsername(), role.getName());
        return getUserRoles(userId);
    }

    @Override
    @Transactional
    public List<UserRoleDto> replaceUserRoles(Long userId, UpdateUserRolesDto dto) {
        User user = getUserOrThrow(userId);
        Set<Long> roleIds = dto.getRoleIds() == null ? Set.of() : dto.getRoleIds();
        Set<Role> targetRoles = new HashSet<>(roleRepository.findAllById(roleIds));

        if (targetRoles.size() != roleIds.size()) {
            throw new AppException(RoleErrorCode.ROLE_NOT_FOUND);
        }

        Set<Role> currentRoles = new HashSet<>(user.getRoles());
        Set<Role> rolesToAdd = targetRoles.stream()
                .filter(role -> !currentRoles.contains(role))
                .collect(Collectors.toSet());
        Set<Role> rolesToRemove = currentRoles.stream()
                .filter(role -> !targetRoles.contains(role))
                .collect(Collectors.toSet());

        for (Role role : rolesToAdd) {
            cognitoGroupService.ensureGroupExists(role.getName());
            cognitoGroupService.addUserToGroup(user.getUsername(), role.getName());
        }

        for (Role role : rolesToRemove) {
            cognitoGroupService.removeUserFromGroup(user.getUsername(), role.getName());
        }

        user.setRoles(targetRoles);
        userRepository.save(user);
        return getUserRoles(userId);
    }

    private Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_NOT_FOUND));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }

    private String normalizeRoleName(String value) {
        if (value == null || value.isBlank()) {
            throw new AppException(RoleErrorCode.ROLE_NAME_REQUIRED);
        }
        return value.trim().toUpperCase().replaceAll("\\s+", "_");
    }

    private Set<Permission> loadPermissions(Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return new HashSet<>();
        }

        return permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new AppException(
                                RoleErrorCode.VALIDATION_ERROR,
                                "Permission not found: " + name)))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public RoleDto create(RoleDto dto) {
        String roleName = normalizeRoleName(dto.getName());
        if (roleRepository.existsByName(roleName)) {
            throw new AppException(RoleErrorCode.ROLE_ALREADY_EXISTS);
        }

        Set<Permission> permissions = loadPermissions(dto.getPermissions());
        cognitoGroupService.createGroup(roleName, dto.getDescription());

        try {
            Role role = roleMapper.toEntity(dto);
            role.setName(roleName);
            role.setPermissions(permissions);
            role = roleRepository.save(role);
            return roleMapper.toDto(role);
        } catch (RuntimeException exception) {
            cognitoGroupService.deleteGroup(roleName);
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getById(Long roleId) {
        return roleMapper.toDto(getRoleOrThrow(roleId));
    }

    @Override
    @Transactional
    public RoleDto update(Long roleId, RoleDto dto) {
        Role role = getRoleOrThrow(roleId);
        String oldName = role.getName();
        String newName = dto.getName() == null || dto.getName().isBlank()
                ? oldName
                : normalizeRoleName(dto.getName());

        if (!oldName.equals(newName) && roleRepository.existsByName(newName)) {
            throw new AppException(RoleErrorCode.ROLE_ALREADY_EXISTS);
        }

        List<User> usersWithRole = userRepository.findByRolesId(roleId);
        if (!oldName.equals(newName)) {
            cognitoGroupService.createGroup(newName, dto.getDescription());
            for (User user : usersWithRole) {
                cognitoGroupService.addUserToGroup(user.getUsername(), newName);
                cognitoGroupService.removeUserFromGroup(user.getUsername(), oldName);
            }
            cognitoGroupService.deleteGroup(oldName);
        }

        roleMapper.updateRoleFromDto(dto, role);
        role.setName(newName);
        if (dto.getPermissions() != null) {
            role.setPermissions(loadPermissions(dto.getPermissions()));
        }

        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(Long roleId) {
        Role role = getRoleOrThrow(roleId);
        if (ADMIN_ROLE.equals(role.getName())) {
            throw new AppException(RoleErrorCode.ROLE_IN_USE, "ADMIN role cannot be deleted");
        }

        List<User> usersWithRole = userRepository.findByRolesId(roleId);
        for (User user : usersWithRole) {
            user.getRoles().remove(role);
            userRepository.save(user);
            cognitoGroupService.removeUserFromGroup(user.getUsername(), role.getName());
        }

        roleRepository.delete(role);
        cognitoGroupService.deleteGroup(role.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAll(Pageable pageable) {
        return getAllRoles(pageable);
    }
}
