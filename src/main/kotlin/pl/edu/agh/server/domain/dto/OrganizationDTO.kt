package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.LogoImage
import java.time.LocalDateTime
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
data class OrganizationDTO(
    var name: String? = null,
    var isSubscribed: Boolean? = null,
    var logoImage: LogoImage? = null,
    var backgroundImage: BackgroundImage? = null,
    var description: String? = null,
    var id: Long? = null,
    var creationDate: Date? = null,
    var lastUpdatedDate: Date? = null,
)
