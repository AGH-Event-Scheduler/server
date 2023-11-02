package pl.edu.agh.server.domain.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import lombok.Data
import lombok.ToString
import java.util.*

@Embeddable
@Data
@ToString
class LogoImage(
    @Column(name = "logoImageId")
    var imageId: UUID,
    @Column(name = "logoSmallFilename")
    var smallFilename: String,
    @Column(name = "logoMediumFilename")
    var mediumFilename: String,
    @Column(name = "logoBigFilename")
    var bigFilename: String,
) {
    companion object {
        val SMALL_SIZE = listOf(500, 500)
        val MEDIUM_SIZE = listOf(1000, 1000)
        val BIG_SIZE = listOf(2000, 2000)
    }
}
