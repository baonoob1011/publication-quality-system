package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.dtos.ResearchGroupMemberDto;
import publication_quality_system.entities.ResearchGroup;
import publication_quality_system.entities.ResearchGroupMember;
import publication_quality_system.entities.User;
import publication_quality_system.enums.MemberRoleInGroup;
import publication_quality_system.enums.MemberStatus;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.ResearchGroupErrorCode;
import publication_quality_system.exceptions.UserErrorCode;
import publication_quality_system.mappers.ResearchGroupMapper;
import publication_quality_system.mappers.ResearchGroupMemberMapper;
import publication_quality_system.repositories.ResearchGroupMemberRepository;
import publication_quality_system.repositories.ResearchGroupRepository;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.ResearchGroupService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResearchGroupServiceImpl implements ResearchGroupService {

    private final ResearchGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ResearchGroupMemberRepository memberRepository;
    private final ResearchGroupMapper mapper;
    private final ResearchGroupMemberMapper memberMapper;

    @Override
    @Transactional
    public ResearchGroupDto create(ResearchGroupDto dto) {
        if (groupRepository.existsByName(dto.getName())) {
            throw new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NAME_ALREADY_EXISTS);
        }
        ResearchGroup group = mapper.toGroupEntity(dto);
        group = groupRepository.save(group);
        return toDto(group, true);
    }

    @Override
    public ResearchGroupDto getById(Long id) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND));
        return toDto(group, true);
    }

    @Override
    @Transactional
    public ResearchGroupDto update(Long id, ResearchGroupDto dto) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND));
        if (dto.getName() != null && !dto.getName().equals(group.getName())
                && groupRepository.existsByName(dto.getName())) {
            throw new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NAME_ALREADY_EXISTS);
        }
        mapper.updateGroupFromDto(dto, group);
        group = groupRepository.save(group);
        return toDto(group, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchGroupDto> getAll(Pageable pageable) {
        return groupRepository.findAllByDeletedFalse(pageable)
                .map(group -> toDto(group, false))
                .getContent();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ResearchGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND));
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public ResearchGroupMemberDto addMember(Long groupId, ResearchGroupMemberDto dto) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        ResearchGroupMember member = memberRepository.findByResearchGroupIdAndUserId(groupId, dto.getUserId())
                .orElse(null);

        if (member != null && MemberStatus.ACTIVE.equals(member.getStatus())) {
            throw new AppException(ResearchGroupErrorCode.GROUP_MEMBER_ALREADY_EXISTS);
        }

        MemberRoleInGroup role = dto.getRole() == null ? MemberRoleInGroup.MEMBER : dto.getRole();
        if (role == MemberRoleInGroup.LEADER) {
            return assignGroupLeader(groupId, dto.getUserId());
        }

        if (member == null) {
            member = ResearchGroupMember.builder()
                    .researchGroup(group)
                    .user(user)
                    .joinedAt(LocalDate.now())
                    .status(MemberStatus.ACTIVE)
                    .role(role)
                    .responsibilities(dto.getResponsibilities())
                    .build();
        } else {
            member.setRole(role);
            member.setStatus(MemberStatus.ACTIVE);
            member.setLeftAt(null);
            member.setResponsibilities(dto.getResponsibilities());
        }

        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId) {
        ResearchGroupMember member = memberRepository.findByResearchGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_MEMBER_NOT_FOUND));

        if (member.getRole() == MemberRoleInGroup.LEADER) {
            throw new AppException(ResearchGroupErrorCode.CANNOT_REMOVE_ONLY_LEADER);
        }

        member.setStatus(MemberStatus.INACTIVE);
        member.setLeftAt(LocalDate.now());
            
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public ResearchGroupMemberDto changeMemberRole(Long groupId, Long userId, ResearchGroupMemberDto dto) {
        if (dto.getRole() == null) throw new AppException(ResearchGroupErrorCode.VALIDATION_ERROR);
        if (dto.getRole() == MemberRoleInGroup.LEADER) return assignGroupLeader(groupId, userId);

        ResearchGroupMember member = memberRepository.findByResearchGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_MEMBER_NOT_FOUND));

        if (member.getRole() == MemberRoleInGroup.LEADER) {
            throw new AppException(ResearchGroupErrorCode.GROUP_LEADER_REQUIRED);
        }

            
        member.setRole(dto.getRole());
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public ResearchGroupMemberDto updateMemberStatus(Long groupId, Long userId, ResearchGroupMemberDto dto) {
        if (dto.getStatus() == null) throw new AppException(ResearchGroupErrorCode.VALIDATION_ERROR);

        ResearchGroupMember member = memberRepository.findByResearchGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.GROUP_MEMBER_NOT_FOUND));

        if (member.getRole() == MemberRoleInGroup.LEADER && dto.getStatus() != MemberStatus.ACTIVE) {
            throw new AppException(ResearchGroupErrorCode.CANNOT_REMOVE_ONLY_LEADER);
        }

        member.setStatus(dto.getStatus());
        member.setLeftAt(dto.getStatus() == MemberStatus.ACTIVE ? null : LocalDate.now());
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public ResearchGroupMemberDto assignGroupLeader(Long groupId, Long userId) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        memberRepository.findByResearchGroupIdAndRole(groupId, MemberRoleInGroup.LEADER)
                .filter(m -> !m.getUser().getId().equals(userId))
                .ifPresent(old -> {
                    old.setRole(MemberRoleInGroup.MEMBER);
                    memberRepository.save(old);
                });

        ResearchGroupMember member = memberRepository.findByResearchGroupIdAndUserId(groupId, userId)
                .orElseGet(() -> ResearchGroupMember.builder()
                        .researchGroup(group)
                        .user(user)
                        .joinedAt(LocalDate.now())
                        .build());

        member.setRole(MemberRoleInGroup.LEADER);
        member.setStatus(MemberStatus.ACTIVE);
        member.setLeftAt(null);
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    public List<ResearchGroupMemberDto> getGroupMembers(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new AppException(ResearchGroupErrorCode.RESEARCH_GROUP_NOT_FOUND);
        }
        return memberRepository.findByResearchGroupId(groupId).stream()
                .map(memberMapper::toDto).toList();
    }

    @Override
    public List<ResearchGroupDto> getGroupsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(UserErrorCode.USER_NOT_FOUND);
        }
        return memberRepository.findByUserId(userId).stream()
                .map(ResearchGroupMember::getResearchGroup)
                .map(group -> toDto(group, false)).toList();
    }

    private ResearchGroupDto toDto(ResearchGroup group, boolean includeMembers) {
        ResearchGroupDto dto = mapper.toGroupDto(group);
        List<ResearchGroupMemberDto> members = memberRepository.findByResearchGroupId(group.getId())
                .stream().map(memberMapper::toDto).toList();
        dto.setMemberCount(members.size());
        dto.setLeader(members.stream()
                .filter(m -> m.getRole() == MemberRoleInGroup.LEADER)
                .findFirst().orElse(null));
        if (includeMembers) dto.setMembers(members);
        return dto;
    }
}
