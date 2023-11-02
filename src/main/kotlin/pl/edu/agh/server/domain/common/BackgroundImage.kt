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
) {
    companion object {
        val SMALL_SIZE = listOf(640, 360)
        val MEDIUM_SIZE = listOf(1280, 720)
        val BIG_SIZE = listOf(1920, 1080)
    }
}
