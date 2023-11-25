package pl.edu.agh.server.application.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.domain.annotation.AuthorizeAccess
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val organizationService: OrganizationService,
) {

    @GetMapping("/organization-roles/{organizationId}")
    fun getLoggedUserOrganizationRoles(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @RequestParam("email", required = false) email: String?,
    ): ResponseEntity<List<OrganizationRole>> {
        val organizationUserRoles: List<OrganizationRole> = when {
            !email.isNullOrBlank() -> {
                userService.getOrganizationRoles(organizationId, email)
            }

            else -> {
                userService.getOrganizationRoles(organizationId, userService.getUserId(request))
            }
        }
        return ResponseEntity.ok(organizationUserRoles)
    }

    @PostMapping("/organization-roles/{organizationId}/grant")
    @AuthorizeAccess(allowedRoles = ["HEAD"])
    fun grantOrganizationRole(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @RequestParam("role") role: OrganizationRole,
        @RequestParam("email") email: String,
    ): ResponseEntity<Void> {
        organizationService.assignUserRole(organizationId, userService.getUserIdByEmail(email), role)
        return ResponseEntity.ok().build()
    }
}
