package publication_quality_system.services.impl;

import publication_quality_system.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.RoleErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.repositories.RoleRepository;
import publication_quality_system.mappers.RoleMapper;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.RoleService;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper mapper;

    @Override
    @Transactional
    public RoleDto create(RoleDto dto) {
        Role role = mapper.toRoleEntity(dto);
        role = roleRepository.save(role);
        return mapper.toRoleDto(role);
    }

    @Override
    public RoleDto getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_NOT_FOUND));
        return mapper.toRoleDto(role);
    }

    @Override
    @Transactional
    public RoleDto update(Long id, RoleDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_NOT_FOUND));
        mapper.updateRoleFromDto(dto, role);
        role = roleRepository.save(role);
        return mapper.toRoleDto(role);
    }


    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_NOT_FOUND));

        user.getRoles().add(role);
        userRepository.save(user);
    }
    @Override
    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

}
