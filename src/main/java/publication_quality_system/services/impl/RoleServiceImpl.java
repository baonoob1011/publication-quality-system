package publication_quality_system.lab_member.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.lab_member.dtos.RoleDto;
import publication_quality_system.lab_member.entities.Role;
import publication_quality_system.lab_member.repositories.RoleRepository;
import publication_quality_system.mappers.RoleMapper;
import publication_quality_system.lab_member.services.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
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
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return mapper.toRoleDto(role);
    }

    @Override
    @Transactional
    public RoleDto update(Long id, RoleDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setDescription(dto.getDescription());
        role = roleRepository.save(role);
        return mapper.toRoleDto(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}
