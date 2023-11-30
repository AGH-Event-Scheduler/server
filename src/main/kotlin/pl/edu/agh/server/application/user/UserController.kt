package pl.edu.agh.server.application.user

import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.annotation.AuthorizeAccess
import pl.edu.agh.server.domain.dto.UserDTO
import pl.edu.agh.server.domain.dto.UserWithRoleDTO
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole
import pl.edu.agh.server.foundation.application.BaseControllerUtilities

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val jwtService: JwtService,
    private val organizationService: OrganizationService,
    private val userRepository: UserRepository,
) : BaseControllerUtilities<User>(jwtService) {

    @GetMapping
    fun getUser(request: HttpServletRequest): UserDTO {
        val user = userService.getUserByEmail(getUserName(request))
        return modelMapper.map(user, UserDTO::class.java)
    }

    @GetMapping("/all/{organizationId}")
    fun getAllUsersUsersWithRoleForOrganization(
        request: HttpServletRequest,
        @RequestParam("search", required = false) search: String?,
        @PathVariable organizationId: Long,
        pageable: Pageable,
    ): Page<UserWithRoleDTO> {
        return userRepository.findAllUsersWithRoleForOrganization(pageable, organizationId)
    }

    @GetMapping("/all")
    fun getAllUsers(
        request: HttpServletRequest,
        @RequestParam("search", required = false) search: String?,
        pageable: Pageable,
    ): Page<UserDTO> {
        return userRepository.findAllUsers(pageable)
    }

    @GetMapping("/organization-roles/{organizationId}")
    fun getLoggedUserOrganizationRoles(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
    ): ResponseEntity<List<OrganizationRole>> {
        return ResponseEntity.ok(userService.getOrganizationRoles(organizationId, userService.getUserId(request)))
    }

    @GetMapping("/organization-roles/{organizationId}/{email}")
    @AuthorizeAccess(allowedRoles = ["ADMIN", "HEAD"])
    fun getUserOrganizationRoles(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @PathVariable email: String,
    ): ResponseEntity<List<OrganizationRole>> {
        return ResponseEntity.ok(userService.getOrganizationRoles(organizationId, email))
    }

    @PostMapping("/organization-roles/{organizationId}/grant")
    @AuthorizeAccess(allowedRoles = ["HEAD"])
    fun grantOrganizationRole(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @RequestParam("role", required = false) role: OrganizationRole?,
        @RequestParam("email") email: String,
    ): ResponseEntity<Void> {
        val userId = userService.getUserIdByEmail(email)

        when (role) {
            null -> organizationService.removeUserRoles(organizationId, userId)
            else -> organizationService.assignUserRole(organizationId, userId, role)
        }

        return ResponseEntity.ok().build()
    }
}
