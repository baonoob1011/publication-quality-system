package publication_quality_system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.enums.PermissionName;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
@SQLDelete(sql = "UPDATE permissions SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionName name;

    private String description;

    @Column(nullable = false)
    private boolean deleted = false;

    private String createdBy;
    private String updatedBy;
}
