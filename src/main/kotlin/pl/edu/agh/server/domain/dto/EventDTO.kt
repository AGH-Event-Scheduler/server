package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.BackgroundImage
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class EventDTO(
    var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var location: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var backgroundImage: BackgroundImage? = null,
    var organization: OrganizationDto? = null,
    var isSaved: Boolean = false,
    var creationDate: String? = null,
    var lastUpdatedDate: String? = null,
)
