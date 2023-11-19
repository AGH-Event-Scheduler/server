package pl.edu.agh.server.domain.user.organizationroles

import pl.edu.agh.server.foundation.domain.BaseRepository

interface OrganizationUserRoleRepository : BaseRepository<OrganizationUserRole> {
    fun findByOrganizationIdAndUserId(organizationId: Long, userId: Long): List<OrganizationUserRole>

    fun existsByOrganizationIdAndUserIdAndRole(organizationId: Long, userId: Long, role: OrganizationRole): Boolean
}
