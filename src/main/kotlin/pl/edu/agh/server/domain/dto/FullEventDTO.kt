package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.translation.LanguageOption
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class FullEventDTO(
    var id: Long? = null,
    var nameMap: Map<LanguageOption, String>? = null,
    var descriptionMap: Map<LanguageOption, String>? = null,
    var locationMap: Map<LanguageOption, String>? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var backgroundImage: BackgroundImage? = null,
    var creationDate: Date? = null,
    var lastUpdatedDate: Date? = null,
)
