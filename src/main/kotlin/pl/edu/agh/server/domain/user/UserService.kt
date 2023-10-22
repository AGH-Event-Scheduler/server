package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.organization.OrganizationRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
) {

    fun isUserSubscribedToOrganization(userName: String, organizationId: Long): Boolean {
        val user = userRepository.findByEmail(userName).orElseThrow { UserNotFoundException(userName) }
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { OrganizationNotFoundException(organizationId) }

        return user.organizations.contains(organization)
    }
}
