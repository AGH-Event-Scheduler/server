package pl.edu.agh.server.domain.notification

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.foundation.application.BaseServiceUtilities

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) : BaseServiceUtilities<Notification>(notificationRepository) {

//    FIXME use function from base service once NullPointerException is fixed
    override fun getAllWithSpecificationPageable(
        specification: Specification<Notification>,
        pageable: PageRequest,
    ): List<Notification> {
        return notificationRepository.findAll(specification, pageable).content
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
    fun notifyAboutEventReenable(event: Event): Notification {
        val newNotification = Notification(
            type = NotificationType.EVENT_REENABLE,
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
}
