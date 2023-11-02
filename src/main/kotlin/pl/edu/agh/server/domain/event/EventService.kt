package pl.edu.agh.server.domain.event

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventFromOrganizationAndInDateRange
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.image.ImageResizeService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.organization.OrganizationRepository
import java.awt.Image
import java.awt.image.BufferedImage
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

@Service
class EventService(private val eventRepository: EventRepository, private val organizationRepository: OrganizationRepository, private val imageStorage: ImageStorage, private val imageResizeService: ImageResizeService) {
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
        id: Long,
        backgroundImage: MultipartFile,
        name: String,
        description: String,
        location: String,
        startDate: Date,
        endDate: Date,
    ): Event {
        val organization = organizationRepository.findById(id).orElseThrow()

        if (!imageStorage.checkIfImageWithProperExtensions(backgroundImage)) {
            throw IncorrectFileUploadException("Uploaded file type is not supported")
        }

        val imageId = imageStorage.generateImageId()
        val extension = backgroundImage.originalFilename?.let { imageStorage.getFileExtension(it) }
        if (extension === "") {
            throw IncorrectFileUploadException("Could not resolve file extension")
        }

        imageStorage.createImageDirectory(imageId)
        val smallName = "small.$extension"
        val mediumName = "medium.$extension"
        val bigName = "big.$extension"

        val originalBufferedImage: BufferedImage = ImageIO.read(backgroundImage.inputStream)

        val smallBackgroundImage: Image = imageResizeService.resize(originalBufferedImage, BackgroundImage.SMALL_SIZE[0], BackgroundImage.SMALL_SIZE[1])
        imageStorage.saveFile(smallBackgroundImage, imageId, smallName)

        val mediumBackgroundImage: Image = imageResizeService.resize(originalBufferedImage, BackgroundImage.MEDIUM_SIZE[0], BackgroundImage.MEDIUM_SIZE[1])
        imageStorage.saveFile(mediumBackgroundImage, imageId, smallName)

        val bigBackgroundImage: Image = imageResizeService.resize(originalBufferedImage, BackgroundImage.BIG_SIZE[0], BackgroundImage.BIG_SIZE[1])
        imageStorage.saveFile(bigBackgroundImage, imageId, smallName)

        imageStorage.saveFile(smallBackgroundImage, imageId, smallName)
        imageStorage.saveFile(mediumBackgroundImage, imageId, mediumName)
        imageStorage.saveFile(bigBackgroundImage, imageId, bigName)

        val newEvent = Event(
            name = name,
            description = description,
            location = location,
            startDate = startDate,
            endDate = endDate,
            organization = organization,
            backgroundImage = BackgroundImage(
                imageId = imageId,
                smallFilename = smallName,
                mediumFilename = mediumName,
                bigFilename = bigName,
            ),
        )
        organization.events.add(newEvent)
        eventRepository.save(newEvent)
        organizationRepository.save(organization)

        return newEvent
    }

    class IncorrectFileUploadException(s: String) : RuntimeException(s)
}
