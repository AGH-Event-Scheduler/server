package pl.edu.agh.server.application.organization

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.OrganizationDto
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.UserOrganizationService
import pl.edu.agh.server.domain.user.User

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationRepository: OrganizationRepository,
    private val userOrganizationService: UserOrganizationService,
    private val jwtService: JwtService,
) {
    // TODO implement lastUpdatedDate updating when patching
    // TODO implement pagination

    @PostMapping("/subscribe")
    fun subscribeUserToOrganization(
        request: HttpServletRequest,
        @RequestParam organizationId: Long,
    ): ResponseEntity<User> {
        val user = userOrganizationService.subscribeUserToOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/unsubscribe")
    fun unsubscribeUserFromOrganization(
        request: HttpServletRequest,
        @RequestParam organizationId: Long,
    ): ResponseEntity<User> {
        val user = userOrganizationService.unsubscribeUserFromOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok(user)
    }

    @GetMapping
    fun getAllOrganizationsWithStatusByUser(
        request: HttpServletRequest,
    ): ResponseEntity<List<OrganizationDto>> {
        val organizations = userOrganizationService.getAllOrganizationsWithStatusByUser(getUserName(request))
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/subscribed")
    fun getSubscribedOrganizationsByUser(
        request: HttpServletRequest,
    ): ResponseEntity<List<OrganizationDto>> {
        val organizations = userOrganizationService.getSubscribedOrganizationsByUser(getUserName(request))
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/{organizationId}")
    fun getOrganizationById(
        @PathVariable organizationId: Long,
    ): ResponseEntity<OrganizationDto> {
        val organization = userOrganizationService.getOrganizationById(organizationId, null)
        return ResponseEntity.ok(organization)
    }

    private fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
