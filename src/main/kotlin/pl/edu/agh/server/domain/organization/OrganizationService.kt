package pl.edu.agh.server.domain.organization

import org.springframework.stereotype.Service

@Service
class OrganizationService(private val organizationRepository: OrganizationRepository) {

    fun updateIsSubscribedStatus(organization: Organization, isSubscribed: Boolean) {
        organization.isSubscribed = isSubscribed
        organizationRepository.save(organization)
    }
}
