package publication_quality_system.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import publication_quality_system.dtos.ResearchGroupMemberDto;
import publication_quality_system.entities.ResearchGroupMember;
import publication_quality_system.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResearchGroupMemberMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(target = "fullName", expression = "java(resolveFullName(member.getUser()))")
    @BeanMapping(builder = @Builder(disableBuilder = true))
    ResearchGroupMemberDto toDto(ResearchGroupMember member);

    default String resolveFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getUsername();
    }
}
