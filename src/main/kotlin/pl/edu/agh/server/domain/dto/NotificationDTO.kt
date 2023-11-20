package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.notification.NotificationType
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class NotificationDTO(
    var id: Long? = null,
    var type: NotificationType? = null,
    var regardingOrganizationDto: OrganizationDTO? = null,
    var regardingEventDTO: EventDTO? = null,
    var seen: Boolean? = null,
    var creationDate: Date? = null,
    var lastUpdatedDate: Date? = null,
)
