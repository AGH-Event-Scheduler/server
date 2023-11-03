package pl.edu.agh.server.application.image

import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.image.ImageService
import java.util.UUID

@RestController
@RequestMapping("/images")
class ImageController(private val imageService: ImageService) {

    @GetMapping("/{imageId}/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getImage(@PathVariable filename: String, @PathVariable imageId: UUID): ResponseEntity<Resource> {
        return ResponseEntity.ok(imageService.getFile(imageId, filename))
    }
}
