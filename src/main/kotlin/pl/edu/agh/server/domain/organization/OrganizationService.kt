package pl.edu.agh.server.domain.organization

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.user.UserDetailsRepository
import pl.edu.agh.server.domain.user.UserDetailsService

@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val userDetailsService: UserDetailsService,
    private val userDetailsRepository: UserDetailsRepository,
) {

    fun updateIsSubscribedStatus(organization: Organization, isSubscribed: Boolean, userId: Long) {
        val userDetails = userDetailsService.getUserDetailsByUserId(userId)
        if (isSubscribed) {
            organization.addSubscriber(userDetails)
        } else {
            organization.removeSubscriber(userDetails)
        }
        organizationRepository.save(organization)
        userDetails.subscribeToOrganization(organization)
        userDetailsRepository.save(userDetails)
    }

    fun isSubscribedByUser(organization: Organization, userId: Long): Boolean {
        return organization.userDetails.map { it.user.id }.contains(userId)
    }
}
