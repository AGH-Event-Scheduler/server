package pl.edu.agh.server.domain.event

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventFromOrganizationAndInDateRange
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.file.ImageStorage
import pl.edu.agh.server.domain.organization.OrganizationRepository
import java.awt.image.BufferedImage
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

@Service
class EventService(private val eventRepository: EventRepository, private val organizationRepository: OrganizationRepository, private val imageStorage: ImageStorage) {
    fun getAllFromOrganizationInDateRange(
        page: Int,
        size: Int,
        sort: String,
        organizationId: Long,
        type: EventsType,
    ): List<Event> {
        val sortParams = sort.split(",")
        val sortBy = sortParams[0]
        val sortDirection = if (sortParams.size > 1) Sort.Direction.fromString(sortParams[1]) else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy))

        return eventRepository
            .findAll(
                eventFromOrganizationAndInDateRange(
                    organizationId,
                    Date.from(Instant.now()),
                    type,
                ),
                pageable,
            ).content
    }

    fun createEvent(
        backgroundImage: MultipartFile,
    ) {
        if (!imageStorage.checkIfImageWithProperExtensions(backgroundImage)) {
            throw IncorrectFileUploadException("Uploaded file type is not supported")
        }

        val imageId = imageStorage.generateImageId()
        val extension = backgroundImage.originalFilename?.let { imageStorage.getFileExtension(it) }
        if (extension === "") {
            throw IncorrectFileUploadException("Could not resolve file extension")
        }

        imageStorage.createImageDirectory(imageId)
        val originalBufferedImage: BufferedImage = ImageIO.read(backgroundImage.inputStream)
//        val minibackgroundImage = serivce.resize
//        val mediumbackgroundImage = serivce.resize
//        val bigbackgroundImage = serivce.resize

        imageStorage.saveFile(originalBufferedImage, imageId, "original.$extension")
//        imageStorage.saveFile(minibackgroundImage)
//        imageStorage.saveFile(mediumbackgroundImage)
//        imageStorage.saveFile(bigbackgroundImage)
//
//        val newEvent = Event()
//        val organizastion.update()
//
//        save(event)
//        save(organization)
//
//        return Event
    }

    class IncorrectFileUploadException(s: String) : RuntimeException(s)
}
