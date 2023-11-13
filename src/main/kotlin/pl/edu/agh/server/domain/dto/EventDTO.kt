package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.BackgroundImage
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class EventDTO(
    var id: Long? = null,
    var nameTranslated: String? = null,
    var descriptionTranslated: String? = null,
    var locationTranslated: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var backgroundImage: BackgroundImage? = null,
    var underOrganization: OrganizationDto? = null,
    var isSaved: Boolean? = null,
    var creationDate: Date? = null,
    var lastUpdatedDate: Date? = null,
)
