package pl.edu.agh.server.domain.user.organizationroles

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "USER_ORGANIZATION_ROLE")
@ToString
@EqualsAndHashCode(callSuper = true)
class OrganizationUserRole(
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "organization_id")
    val organization: Organization,

    @Enumerated(EnumType.STRING)
    val role: OrganizationRole,
) : BaseIdentifiableEntity()
