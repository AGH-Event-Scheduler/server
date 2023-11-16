package pl.edu.agh.server.domain.notification

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.dto.NotificationDTO
import pl.edu.agh.server.domain.dto.OrganizationDto
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.application.BaseServiceUtilities

@Service
class NotificationDTOTranslateService(
    private val notificationRepository: NotificationRepository,
    private val modelMapper: ModelMapper,
    private val organizationService: OrganizationService,
    private val eventService: EventService,
) : BaseServiceUtilities<Notification>(notificationRepository) {

    fun transformToNotificationDTO(notifications: List<Notification>, language: LanguageOption, user: User): List<NotificationDTO> {
        val eventDtoMap = getEventDtoMap(notifications, language, user.email)
        val organizationDtoMap = getOrganizationDtoMap(notifications, language, user.email)

        return notifications.map {
            modelMapper.map(it, NotificationDTO::class.java).apply {
                regardingOrganizationDto = if (it.regardingOrganization != null) organizationDtoMap[it.regardingOrganization!!.id] else null
                regardingEventDTO = if (it.regardingEvent != null) eventDtoMap[it.regardingEvent!!.id] else null
                seen = user.seenNotifications.contains(it)
            }
        }
    }

    fun transformToNotificationDTO(notification: Notification, language: LanguageOption, user: User): NotificationDTO {
        return transformToNotificationDTO(listOf(notification), language, user).first()
    }

    private fun getOrganizationDtoMap(notifications: List<Notification>, language: LanguageOption, userName: String?): Map<Long, OrganizationDto> {
        val organizations = mutableSetOf<Organization>()
        notifications.forEach {
            if (it.regardingOrganization != null) {
                organizations.add(it.regardingOrganization!!)
            }
        }

        val organizationDTOs = organizationService.transformToOrganizationDTO(organizations.toList(), language, userName)
        return organizationDTOs.associateBy({ it.id!! }, { it })
    }

    private fun getEventDtoMap(notifications: List<Notification>, language: LanguageOption, userName: String?): Map<Long, EventDTO> {
        val events = mutableSetOf<Event>()
        notifications.forEach {
            if (it.regardingEvent != null) {
                events.add(it.regardingEvent!!)
            }
        }
        val eventDTOS = eventService.transformToEventDTO(events.toList(), language, userName)
        return eventDTOS.associateBy({ it.id!! }, { it })
    }
}
