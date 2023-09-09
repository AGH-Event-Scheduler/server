package pl.edu.agh.server.application.organization

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController
import javax.validation.Valid

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
  private val organizationRepository: OrganizationRepository,
  private val organizationService: OrganizationService
) : BaseIdentifiableCrudController<Organization>(organizationRepository) {

  @PatchMapping("/{id}/subscription")
  fun updateSubscriptionStatus(
    @PathVariable id: Long,
    @Valid @RequestBody updatedStatus: Boolean
  ): ResponseEntity<Unit> {
    val organization = organizationRepository.findById(id)
    return organization.map { org ->
      organizationService.updateIsSubscribedStatus(org, updatedStatus)
      ResponseEntity.ok<Unit>(Unit)
    }.orElseGet {
      ResponseEntity.notFound().build()
    }
  }
}
