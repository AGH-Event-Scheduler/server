package pl.edu.agh.server.domain.user.organizationroles

import pl.edu.agh.server.foundation.domain.BaseRepository

interface UserOrganizationRoleRepository : BaseRepository<UserOrganizationRole> {
    fun findByOrganizationIdAndUserId(organizationId: Long, userId: Long): List<UserOrganizationRole>

    fun existsByOrganizationIdAndUserIdAndRole(organizationId: Long, userId: Long, role: OrganizationRole): Boolean
}
