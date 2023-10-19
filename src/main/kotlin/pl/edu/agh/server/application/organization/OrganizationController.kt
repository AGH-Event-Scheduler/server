package pl.edu.agh.server.application.organization

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import javax.validation.Valid

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationRepository: OrganizationRepository,
    private val organizationService: OrganizationService,
) {

    @GetMapping
    fun getOrganizations(): ResponseEntity<List<Organization>> {
        return ResponseEntity.ok(organizationRepository.findAll())
    }

    @GetMapping("/{userId}")
    fun getOrganizationsForUser(@PathVariable userId: Long): ResponseEntity<List<Organization>> {
        return ResponseEntity.ok(getOrganizationsWithSubscription(userId))
    }

    @GetMapping("/{userId}/subscribed")
    fun getOrganizationsSubscribedForUser(@PathVariable userId: Long): ResponseEntity<List<Organization>> {
        return ResponseEntity.ok(getSubscribedOrganizations(userId))
    }

    @GetMapping("/details/{id}")
    fun getOrganizationById(@PathVariable id: Long): ResponseEntity<Organization> {
        return organizationRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @GetMapping("/details/{id}/{userId}")
    fun getOrganizationByIdAndUserId(@PathVariable id: Long, @PathVariable userId: Long): ResponseEntity<Organization> {
        return organizationRepository.findById(id)
            .map {
                it.apply { isSubscribed = organizationService.isSubscribedByUser(it, userId) }
                ResponseEntity.ok(it)
            }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PatchMapping("/details/{id}/subscription/{userId}")
    fun updateSubscriptionStatus(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        @Valid @RequestBody updatedStatus: Boolean,
    ): ResponseEntity<Unit> {
        return organizationRepository.findById(id)
            .map {
                organizationService.updateIsSubscribedStatus(it, updatedStatus, userId)
                ResponseEntity.ok(Unit)
            }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    private fun getOrganizationsWithSubscription(userId: Long): List<Organization> {
        return organizationRepository.findAll().map { organization ->
            organization.apply {
                isSubscribed = organizationService.isSubscribedByUser(organization, userId)
            }
        }
    }

    private fun getSubscribedOrganizations(userId: Long): List<Organization> {
        return organizationRepository.findAll()
            .filter { organization -> organizationService.isSubscribedByUser(organization, userId) }
            .map { organization ->
                organization.apply {
                    isSubscribed = true
                }
            }
    }
}
