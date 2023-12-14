package pl.edu.agh.server.application.organization

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.annotation.AdminRestricted
import pl.edu.agh.server.domain.annotation.AuthorizeOrganizationAccess
import pl.edu.agh.server.domain.dto.FullOrganizationDTO
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
    ): ResponseEntity<Page<OrganizationDTO>> {
        val pageRequest = createPageRequest(page, size, sort)
        val organizationsPage: Page<Organization> = organizationService.getAllWithSpecificationPageable(
            Specification.allOf(
                if (yourOrganizationsOnly) organizationsWithAuthority(getUserName(request)) else null,
                if (subscribedOnly) organizationFollowedByUser(getUserName(request)) else null,
                if (name != null) organizationWithNameLike(name, language) else null,
            ),
            pageRequest,
        )
        val organizationsDTOPage = PageImpl(
            organizationService.transformToOrganizationDTO(
                organizationsPage.content,
                language,
                getUserName(request),
            ),
            pageRequest,
            organizationsPage.totalElements,
        )
        return ResponseEntity.ok(organizationsDTOPage)
    }

    @PostMapping
    @AdminRestricted
    fun createOrganization(
        request: HttpServletRequest,
        @ModelAttribute createOrganizationRequest: CreateOrganizationRequest,
    ): ResponseEntity<OrganizationDTO> {
        val objectMapper = jacksonObjectMapper()
        val nameMap: Map<LanguageOption, String> = objectMapper.readValue(createOrganizationRequest.name)
        val descriptionMap: Map<LanguageOption, String> = objectMapper.readValue(createOrganizationRequest.description)

        val organization = organizationService.createOrganization(
            logoImageFile = createOrganizationRequest.logoImage,
            backgroundImageFile = createOrganizationRequest.backgroundImage,
            nameMap = nameMap,
            descriptionMap = descriptionMap,
            leaderEmail = createOrganizationRequest.leaderEmail,
        )

        return ResponseEntity.ok(organizationService.transformToOrganizationDTO(organization, LanguageOption.PL, getUserName(request)))
    }

    @GetMapping("/{organizationId}")
    fun getOrganizationById(
        request: HttpServletRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable organizationId: Long,
    ): ResponseEntity<OrganizationDTO> {
        val organization = organizationService.getOrganization(organizationId)
        return ResponseEntity.ok(
            organizationService.transformToOrganizationDTO(
                organization,
                language,
                getUserName(request),
            ),
        )
    }

    @GetMapping("/{organizationId}/full")
    @AuthorizeOrganizationAccess(allowedRoles = ["HEAD", "ADMIN"])
    fun getFullOrganizationById(
        @PathVariable organizationId: Long,
    ): ResponseEntity<FullOrganizationDTO> {
        val organization = organizationService.getOrganization(organizationId)
        return ResponseEntity.ok(
            organizationService.transformToFullOrganizationDTO(
                organization,
            ),
        )
    }

    @PutMapping("/{organizationId}")
    @AuthorizeOrganizationAccess(allowedRoles = ["HEAD", "ADMIN"])
    fun updateOrganization(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @RequestBody organizationUpdateRequest: UpdateOrganizationRequest,
    ): ResponseEntity<OrganizationDTO> {
        val objectMapper = jacksonObjectMapper()
        val nameMap: Map<LanguageOption, String> = objectMapper.readValue(organizationUpdateRequest.name)
        val descriptionMap: Map<LanguageOption, String> = objectMapper.readValue(organizationUpdateRequest.description)

        val organization = organizationService.updateOrganization(
            organizationId = organizationId,
            backgroundImageFile = organizationUpdateRequest.backgroundImage,
            logoImageFile = organizationUpdateRequest.logoImage,
            nameMap = nameMap,
            descriptionMap = descriptionMap,
        )

        return ResponseEntity.ok(
            organizationService.transformToOrganizationDTO(
                organization,
                LanguageOption.PL,
                getUserName(request),
            ),
        )
    }
}
