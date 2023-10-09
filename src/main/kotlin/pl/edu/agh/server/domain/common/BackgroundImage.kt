package pl.edu.agh.server.domain.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import lombok.Data
import lombok.ToString

@Embeddable
@Data
@ToString
class BackgroundImage(
    @Column(length = 500, name = "backgroundSmallUrl")
    var smallUrl: String,
    @Column(length = 500, name = "backgroundMediumUrl")
    var mediumUrl: String,
    @Column(length = 500, name = "backgroundBigUrl")
    var bigUrl: String,
)
