package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.ResearchProfileDto;
import publication_quality_system.entities.ResearchProfile;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.ResearchProfileErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.repositories.ResearchProfileRepository;
import publication_quality_system.mappers.ResearchProfileMapper;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.ResearchProfileService;

@Service
@RequiredArgsConstructor
public class ResearchProfileServiceImpl implements ResearchProfileService {

    private final ResearchProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ResearchProfileMapper mapper;

    @Override
    @Transactional
    public ResearchProfileDto create(ResearchProfileDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        ResearchProfile profile = mapper.toProfileEntity(dto);
        profile.setUser(user);
        profile = profileRepository.save(profile);
        return mapper.toProfileDto(profile);
    }

    @Override
    public ResearchProfileDto getById(Long id) {
        ResearchProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchProfileErrorCode.PROFILE_NOT_FOUND));
        return mapper.toProfileDto(profile);
    }

    @Override
    public ResearchProfileDto getByUserId(Long userId) {
        ResearchProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ResearchProfileErrorCode.PROFILE_NOT_FOUND));
        return mapper.toProfileDto(profile);
    }

    @Override
    @Transactional
    public ResearchProfileDto update(Long id, ResearchProfileDto dto) {
        ResearchProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchProfileErrorCode.PROFILE_NOT_FOUND));
        mapper.updateProfileFromDto(dto, profile);
        profile = profileRepository.save(profile);
        return mapper.toProfileDto(profile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }
}
