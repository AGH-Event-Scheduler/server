package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val eventRepository: EventRepository,
) {
    fun getSubscribedOrganizationsByUser(userName: String): MutableSet<Organization> {
        val user = userRepository.findByEmail(userName).orElseThrow { UserNotFoundException(userName) }
        return user.followedOrganizations
    }

    fun getEventsSavedByUser(userName: String): MutableSet<Event> {
        val user = userRepository.findByEmail(userName).orElseThrow { UserNotFoundException(userName) }
        return user.savedEvents
    }
}
