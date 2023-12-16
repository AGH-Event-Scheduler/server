package pl.edu.agh.server.domain.notification

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.exception.NotificationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.foundation.application.BaseServiceUtilities

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) : BaseServiceUtilities<Notification>(notificationRepository) {

    @Transactional
    fun markNotificationAsSeen(userName: String, notificationId: Long) {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { throw NotificationNotFoundException(notificationId) }

        notification.seenByUsers.add(user)
        notificationRepository.save(notification)
    }

//    FIXME use function from base service once NullPointerException is fixed
    override fun getAllWithSpecificationPageable(
        specification: Specification<Notification>,
        pageable: PageRequest,
    ): Page<Notification> {
        return notificationRepository.findAll(specification, pageable)
    }

    @Transactional
    fun notifyAboutEventCreation(event: Event): Notification {
        val newNotification = Notification(
            type = NotificationType.EVENT_CREATE,
            regardingEvent = event,
            forFollowersOfOrganizations = mutableSetOf(event.organization),
            forWritersOfOrganizations = mutableSetOf(event.organization),
            forDirectorsOfOrganizations = mutableSetOf(event.organization),
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutEventUpdate(event: Event): Notification {
        val newNotification = Notification(
            type = NotificationType.EVENT_UPDATE,
            regardingEvent = event,
            forUsersWithSavedEvents = mutableSetOf(event),
            forFollowersOfOrganizations = mutableSetOf(event.organization),
            forWritersOfOrganizations = mutableSetOf(event.organization),
            forDirectorsOfOrganizations = mutableSetOf(event.organization),
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutEventCancel(event: Event): Notification {
        val newNotification = Notification(
            type = NotificationType.EVENT_CANCEL,
            regardingEvent = event,
            forUsersWithSavedEvents = mutableSetOf(event),
            forFollowersOfOrganizations = mutableSetOf(event.organization),
            forWritersOfOrganizations = mutableSetOf(event.organization),
            forDirectorsOfOrganizations = mutableSetOf(event.organization),
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutEventReactivate(event: Event): Notification {
        val newNotification = Notification(
            type = NotificationType.EVENT_REACTIVATE,
            regardingEvent = event,
            forUsersWithSavedEvents = mutableSetOf(event),
            forFollowersOfOrganizations = mutableSetOf(event.organization),
            forWritersOfOrganizations = mutableSetOf(event.organization),
            forDirectorsOfOrganizations = mutableSetOf(event.organization),
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutOrganizationCreation(organization: Organization): Notification {
        val newNotification = Notification(
            type = NotificationType.ORGANIZATION_CREATE,
            regardingOrganization = organization,
            forAllUsers = true,
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutOrganizationUpdate(organization: Organization): Notification {
        val newNotification = Notification(
            type = NotificationType.ORGANIZATION_UPDATE,
            regardingOrganization = organization,
            forFollowersOfOrganizations = mutableSetOf(organization),
            forWritersOfOrganizations = mutableSetOf(organization),
            forDirectorsOfOrganizations = mutableSetOf(organization),
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutOrganizationArchive(organization: Organization): Notification {
        val newNotification = Notification(
            type = NotificationType.ORGANIZATION_ARCHIVE,
            regardingOrganization = organization,
            forAllUsers = true,
        )

        return notificationRepository.save(newNotification)
    }

    @Transactional
    fun notifyAboutOrganizationReactivate(organization: Organization): Notification {
        val newNotification = Notification(
            type = NotificationType.ORGANIZATION_REACTIVATE,
            regardingOrganization = organization,
            forAllUsers = true,
        )

        return notificationRepository.save(newNotification)
    }
}
