package pl.edu.agh.server.domain.dto

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.common.LogoImage
import java.time.LocalDateTime

@AllArgsConstructor
@NoArgsConstructor
data class OrganizationDto(
    var name: String? = null,
    var isSubscribed: Boolean? = null,
    var logoImage: LogoImage? = null,
    var backgroundImage: BackgroundImage? = null,
    var description: String? = null,
    var id: Long? = null,
    var creationDate: LocalDateTime? = null,
    var lastUpdatedDate: LocalDateTime? = null,
)