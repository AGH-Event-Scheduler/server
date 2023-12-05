package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.translation.LanguageOption
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class FullOrganizationDTO(
    var id: Long? = null,
    var nameMap: Map<LanguageOption, String>? = null,
    var descriptionMap: Map<LanguageOption, String>? = null,
    var backgroundImage: BackgroundImage? = null,
    var logoImage: LogoImage? = null,
    var creationDate: Date? = null,
    var lastUpdatedDate: Date? = null,
)
