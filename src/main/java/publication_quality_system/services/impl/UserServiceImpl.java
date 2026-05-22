package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.UserDto;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.RoleErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.repositories.RoleRepository;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.mappers.UserMapper;
import publication_quality_system.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new AppException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User user = mapper.toUserEntity(dto);
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user = userRepository.save(user);
        return mapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        mapper.updateUserFromDto(dto, user);
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user = userRepository.save(user);
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
}

