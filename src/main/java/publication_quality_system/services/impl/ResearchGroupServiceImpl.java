package publication_quality_system.lab_member.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.lab_member.dtos.ResearchGroupDto;
import publication_quality_system.lab_member.entities.ResearchGroup;
import publication_quality_system.lab_member.entities.User;
import publication_quality_system.lab_member.repositories.ResearchGroupRepository;
import publication_quality_system.mappers.ResearchGroupMapper;
import publication_quality_system.lab_member.repositories.UserRepository;
import publication_quality_system.lab_member.services.ResearchGroupService;

@Service
@RequiredArgsConstructor
public class ResearchGroupServiceImpl implements ResearchGroupService {

    private final ResearchGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ResearchGroupMapper mapper;

    @Override
    @Transactional
    public ResearchGroupDto create(ResearchGroupDto dto) {
        ResearchGroup group = mapper.toGroupEntity(dto);
        group = groupRepository.save(group);
        return mapper.toGroupDto(group);
    }

    @Override
    public ResearchGroupDto getById(Long id) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return mapper.toGroupDto(group);
    }

    @Override
    @Transactional
    public ResearchGroupDto update(Long id, ResearchGroupDto dto) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        mapper.updateGroupFromDto(dto, group);
        group = groupRepository.save(group);
        return mapper.toGroupDto(group);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignLeader(Long groupId, Long userId) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.setLeader(user);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void addMember(Long groupId, Long userId) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.getMembers().add(user);
        groupRepository.save(group);
    }
}
