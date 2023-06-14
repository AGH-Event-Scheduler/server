package pl.edu.agh.server.organization

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizations")
class OrganizationController(private val organizationService: OrganizationService) {

  @GetMapping
  fun getAllOrganizations(): List<Organization> {
    return organizationService.getAllOrganizations()
  }

  @PostMapping
  fun createOrganization(@RequestBody organization: Organization): ResponseEntity<Organization> {
    val createdOrganization = organizationService.createOrganization(organization)
    return ResponseEntity(createdOrganization, HttpStatus.CREATED)
  }

  @PutMapping("/{id}")
  fun updateOrganization(@PathVariable id: Long, @RequestBody organization: Organization): ResponseEntity<Organization> {
    return organizationService.updateOrganization(id, organization)?.let {
      ResponseEntity.ok(it)
    } ?: ResponseEntity.notFound().build()
  }

  @PutMapping("/{id}/subscription")
  fun updateSubscriptionStatus(@PathVariable id: Long, @RequestBody updatedStatus: Boolean): ResponseEntity<Unit> {
    return organizationService.getOrganization(id)?.let { organization ->
      organization.isSubscribed = updatedStatus
      organizationService.updateOrganization(id, organization)
      ResponseEntity.noContent().build()
    } ?: ResponseEntity.notFound().build()
  }
}