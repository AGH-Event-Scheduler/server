package pl.edu.agh.server.application.organization

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.organization.OrganizationSpecification.Companion.organizationFollowedByUser
import pl.edu.agh.server.domain.organization.OrganizationSpecification.Companion.organizationWithNameLike
import pl.edu.agh.server.domain.organization.OrganizationSpecification.Companion.organizationsWithAuthority
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.foundation.application.BaseControllerUtilities
import java.util.*

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
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
        @RequestParam(name = "subscribedOnly", defaultValue = false.toString()) subscribedOnly: Boolean,
        @RequestParam(name = "yourOrganizationsOnly", defaultValue = false.toString()) yourOrganizationsOnly: Boolean,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        request: HttpServletRequest,
    ): ResponseEntity<List<OrganizationDTO>> {
        val organizations = organizationService.transformToOrganizationDTO(
            organizationService.getAllWithSpecificationPageable(
                Specification.allOf(
                    if (yourOrganizationsOnly) organizationsWithAuthority(getUserName(request)) else null,
                    if (subscribedOnly) organizationFollowedByUser(getUserName(request)) else null,
                    if (name != null) organizationWithNameLike(name, language) else null,
                ),
                createPageRequest(page, size, sort),
            ),
            language,
            getUserName(request),
        )
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/{organizationId}")
    fun getOrganizationById(
        request: HttpServletRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable organizationId: Long,
    ): ResponseEntity<OrganizationDTO> {
        val organization = organizationService.getOrganization(organizationId, getUserName(request))
        return ResponseEntity.ok(
            organizationService.transformToOrganizationDTO(
                organization,
                language,
                getUserName(request),
            ),
        )
    }

    @PostMapping
    fun createOrganization(
        @RequestBody createOrganizationRequest: CreateOrganizationRequest,
        request: HttpServletRequest
    ): ResponseEntity<OrganizationDTO> {
        val objectMapper = jacksonObjectMapper()
        val nameMap: Map<LanguageOption, String> = objectMapper.readValue(createOrganizationRequest.name)
        val descriptionMap: Map<LanguageOption, String> = objectMapper.readValue(createOrganizationRequest.description)

        val organization = organizationService.createOrganization(
            logoImageFile = createOrganizationRequest.logoImage,
            backgroundImageFile = createOrganizationRequest.backgroundImage,
            nameMap = nameMap,
            descriptionMap = descriptionMap,
        )

        return ResponseEntity.ok(organizationService.transformToOrganizationDTO(organization, LanguageOption.PL, getUserName(request)))
    }
}
