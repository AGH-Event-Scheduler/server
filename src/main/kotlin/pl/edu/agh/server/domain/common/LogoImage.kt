package pl.edu.agh.server.domain.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import lombok.Data
import lombok.ToString

@Embeddable
@Data
@ToString
class LogoImage(
    @Column(length = 500, name = "logoSmallUrl")
    var smallUrl: String,
    @Column(length = 500, name = "logoMediumUrl")
    var mediumUrl: String,
    @Column(length = 500, name = "logoBigUrl")
    var bigUrl: String,
)
