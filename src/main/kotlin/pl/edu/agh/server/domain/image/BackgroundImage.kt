package pl.edu.agh.server.domain.image

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import lombok.Data
import lombok.ToString
import java.util.UUID

@Embeddable
@Data
@ToString
class BackgroundImage(
    @Column(name = "backgroundImageId")
    var imageId: UUID,
    @Column(name = "backgroundSmallFilename")
    var smallFilename: String,
    @Column(name = "backgroundMediumFilename")
    var mediumFilename: String,
    @Column(name = "backgroundBigFilename")
    var bigFilename: String,
) {
    companion object {
        val SMALL_SIZE = listOf(640, 360)
        val MEDIUM_SIZE = listOf(1280, 720)
        val BIG_SIZE = listOf(1920, 1080)
    }
}
