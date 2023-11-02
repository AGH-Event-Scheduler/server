package pl.edu.agh.server.application.image

import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.image.ImageStorage
import java.util.UUID
import javax.management.InvalidAttributeValueException

@RestController
@RequestMapping("/images")
class ImageController(private val imageStorage: ImageStorage) {

    @GetMapping("/{imageId}/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getImage(@PathVariable filename: String, @PathVariable imageId: UUID): ResponseEntity<Resource> {
        println(filename)
        if (!filename.startsWith("small") && !filename.startsWith("medium") && !filename.startsWith("big")) {
            throw InvalidAttributeValueException("Incorrect filename")
        }
        return ResponseEntity.ok(imageStorage.getFile(imageId, filename))
    }
}
