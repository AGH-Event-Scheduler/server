package pl.edu.agh.server.domain.user.organizationroles

import org.springframework.data.jpa.repository.Query
import pl.edu.agh.server.foundation.domain.BaseRepository

interface OrganizationUserRoleRepository : BaseRepository<OrganizationUserRole> {
    fun findByOrganizationIdAndUserId(organizationId: Long, userId: Long): List<OrganizationUserRole>

    @Query("SELECT role FROM OrganizationUserRole WHERE organization.id = :organizationId AND user.id = :userId")
    fun findOrganizationRoleByOrganizationIdAndUserId(organizationId: Long, userId: Long): List<OrganizationRole>

    fun existsByOrganizationIdAndUserIdAndRole(organizationId: Long, userId: Long, role: OrganizationRole): Boolean
}
