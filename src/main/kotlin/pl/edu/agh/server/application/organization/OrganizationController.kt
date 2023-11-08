package pl.edu.agh.server.application.organization

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.OrganizationDto
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.organization.OrganizationSpecification.Companion.organizationFollowedByUser
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.foundation.application.BaseControllerUtilities

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationRepository: OrganizationRepository,
    private val organizationService: OrganizationService,
    private val jwtService: JwtService,
) : BaseControllerUtilities<Organization>(jwtService) {
    // TODO implement lastUpdatedDate updating when patching

    @PostMapping("/subscribe")
    fun subscribeUserToOrganization(
        request: HttpServletRequest,
        @RequestParam organizationId: Long,
    ): ResponseEntity<Void> {
        organizationService.subscribeUserToOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/unsubscribe")
    fun unsubscribeUserFromOrganization(
        request: HttpServletRequest,
        @RequestParam organizationId: Long,
    ): ResponseEntity<Void> {
        organizationService.unsubscribeUserFromOrganization(getUserName(request), organizationId)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getAllOrganizationsWithStatusByUser(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "id,desc") sort: String,
        request: HttpServletRequest,
    ): ResponseEntity<List<OrganizationDto>> {
        val organizations = organizationService.transformToOrganizationDTO(
            organizationService.getAllWithPageable(createPageRequest(page, size, sort)),
            LanguageOption.PL,
            getUserName(request),
        )
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/subscribed")
    fun getSubscribedOrganizationsByUser(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "id,desc") sort: String,
        request: HttpServletRequest,
    ): ResponseEntity<List<OrganizationDto>> {
        val organizations = organizationService.transformToOrganizationDTO(
            organizationService.getAllWithSpecificationPageable(organizationFollowedByUser(getUserName(request)), createPageRequest(page, size, sort)),
            LanguageOption.PL,
            getUserName(request),
        )
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/{organizationId}")
    fun getOrganizationById(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
    ): ResponseEntity<OrganizationDto> {
        val organization = organizationService.getOrganization(organizationId, getUserName(request))
        return ResponseEntity.ok(
            organizationService.transformToOrganizationDTO(
                organization,
                LanguageOption.PL,
                getUserName(request),
            ).orElseThrow { throw OrganizationNotFoundException(organizationId) },
        )
    }
}
