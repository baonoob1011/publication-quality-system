package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.entities.ResearchGroup;
import publication_quality_system.entities.ResearchGroupMember;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.ResearchGroupErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.repositories.ResearchGroupRepository;
import publication_quality_system.mappers.ResearchGroupMapper;
import publication_quality_system.repositories.ResearchGroupMemberRepository;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.ResearchGroupService;

@Service
@RequiredArgsConstructor
public class ResearchGroupServiceImpl implements ResearchGroupService {

    private final ResearchGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ResearchGroupMemberRepository memberRepository;
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
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_NOT_FOUND));
        return mapper.toGroupDto(group);
    }

    @Override
    @Transactional
    public ResearchGroupDto update(Long id, ResearchGroupDto dto) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_NOT_FOUND));
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
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        group.setLeader(user);
        groupRepository.save(group);
        ResearchGroupMember membership = memberRepository.findByResearchGroupAndUser(group, user)
                .orElseGet(() -> {
                    ResearchGroupMember member = new ResearchGroupMember();
                    member.setResearchGroup(group);
                    member.setUser(user);
                    return member;
                });
        membership.setLeader(true);
        memberRepository.save(membership);
    }

    @Override
    @Transactional
    public void addMember(Long groupId, Long userId) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        if (memberRepository.findByResearchGroupAndUser(group, user).isEmpty()) {
            ResearchGroupMember member = new ResearchGroupMember();
            member.setResearchGroup(group);
            member.setUser(user);
            memberRepository.save(member);
        }
    }
}
