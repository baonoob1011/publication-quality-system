package publication_quality_system.lab_member.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.lab_member.dtos.ResearchProfileDto;
import publication_quality_system.lab_member.entities.ResearchProfile;
import publication_quality_system.lab_member.entities.User;
import publication_quality_system.lab_member.repositories.ResearchProfileRepository;
import publication_quality_system.mappers.ResearchProfileMapper;
import publication_quality_system.lab_member.repositories.UserRepository;
import publication_quality_system.lab_member.services.ResearchProfileService;

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
                .orElseThrow(() -> new RuntimeException("User not found"));
        ResearchProfile profile = mapper.toProfileEntity(dto);
        profile.setUser(user);
        profile = profileRepository.save(profile);
        return mapper.toProfileDto(profile);
    }

    @Override
    public ResearchProfileDto getById(Long id) {
        ResearchProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return mapper.toProfileDto(profile);
    }

    @Override
    public ResearchProfileDto getByUserId(Long userId) {
        ResearchProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"));
        return mapper.toProfileDto(profile);
    }

    @Override
    @Transactional
    public ResearchProfileDto update(Long id, ResearchProfileDto dto) {
        ResearchProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
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
