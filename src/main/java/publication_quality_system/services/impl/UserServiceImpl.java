package publication_quality_system.lab_member.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.lab_member.dtos.UserDto;
import publication_quality_system.lab_member.entities.Role;
import publication_quality_system.lab_member.repositories.RoleRepository;
import publication_quality_system.lab_member.repositories.UserRepository;
import publication_quality_system.mappers.UserMapper;
import publication_quality_system.lab_member.repositories.UserRepository;
import publication_quality_system.lab_member.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = mapper.toUserEntity(dto);
        user = userRepository.save(user);
        return mapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        mapper.updateUserFromDto(dto, user);
        user = userRepository.save(user);
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }
}
