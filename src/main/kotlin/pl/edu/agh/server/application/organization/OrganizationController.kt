package pl.edu.agh.server.application.organization

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.UserOrganizationService
import pl.edu.agh.server.domain.translation.LanguageOption

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
    ): ResponseEntity<Void> {
        userOrganizationService.subscribeUserToOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/unsubscribe")
    fun unsubscribeUserFromOrganization(
        request: HttpServletRequest,
        @RequestParam organizationId: Long,
    ): ResponseEntity<Void> {
        userOrganizationService.unsubscribeUserFromOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getAllOrganizationsWithStatusByUser(
        request: HttpServletRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        ): ResponseEntity<List<OrganizationDTO>> {
        val organizations = userOrganizationService.getAllOrganizationsWithStatusByUser(getUserName(request), language)
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/subscribed")
    fun getSubscribedOrganizationsByUser(
        request: HttpServletRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        ): ResponseEntity<List<OrganizationDTO>> {
        val organizations = userOrganizationService.getSubscribedOrganizationsByUser(getUserName(request), language)
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/{organizationId}")
    fun getOrganizationById(
        request: HttpServletRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable organizationId: Long,
    ): ResponseEntity<OrganizationDTO> {
        val organization = userOrganizationService.getOrganizationById(organizationId, getUserName(request), language)
        return ResponseEntity.ok(organization)
    }

    private fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
