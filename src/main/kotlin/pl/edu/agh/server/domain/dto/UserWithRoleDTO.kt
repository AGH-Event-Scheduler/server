package pl.edu.agh.server.domain.dto

import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole

data class UserWithRoleDTO(
    var email: String? = null,
    var name: String? = null,
    var lastname: String? = null,
    var role: OrganizationRole? = null,
    var organizationId: Long? = null,
)
