package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.notification.NotificationType

@AllArgsConstructor
@NoArgsConstructor
data class NotificationDTO(
    var type: NotificationType? = null,
    var logoImage: LogoImage? = null,
    var regardingOrganizationDto: OrganizationDTO? = null,
    var regardingEventDTO: EventDTO? = null,
    var seen: Boolean? = null,
)
