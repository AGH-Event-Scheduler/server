package pl.edu.agh.server.organization

import org.springframework.stereotype.Service

@Service
class OrganizationService(private val organizationRepository: OrganizationRepository) {

  fun createOrganization(organization: Organization): Organization =
      organizationRepository.save(organization)

  fun updateOrganization(id: Long, organization: Organization): Organization? =
      organizationRepository.findById(id)
          .map { existingOrganization ->
            val updatedOrganization = existingOrganization.copy(id = id)
            organizationRepository.save(updatedOrganization)
          }
          .orElse(null)

  fun getAllOrganizations(): List<Organization> =
      organizationRepository.findAll().sortedWith(compareByDescending {it.name})

  fun getOrganization(id: Long): Organization? =
      organizationRepository.findById(id).orElse(null)
}
