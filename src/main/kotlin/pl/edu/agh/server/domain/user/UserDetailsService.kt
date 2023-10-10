package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository

@Service
class UserDetailsService(
    val userDetailsRepository: UserDetailsRepository,
    val organizationRepository: OrganizationRepository,
) {

    fun subscribeToOrganization(userDetails: UserDetails, organization: Organization) {
        userDetails.subscribeToOrganization(organization)
        userDetailsRepository.save(userDetails)
        organization.addSubscriber(userDetails)
        organizationRepository.save(organization)
    }
}
