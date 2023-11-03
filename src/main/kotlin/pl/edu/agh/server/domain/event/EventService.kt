package pl.edu.agh.server.domain.event

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventFromOrganizationAndInDateRange
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.organization.OrganizationRepository
import java.time.Instant
import java.util.*

@Service
class EventService(private val eventRepository: EventRepository, private val organizationRepository: OrganizationRepository, private val imageStorage: ImageStorage, private val imageService: ImageService) {
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

        val savedBackgroundImage: BackgroundImage = imageService.createBackgroundImage(backgroundImage)

        val newEvent = Event(
            name = name,
            description = description,
            location = location,
            startDate = startDate,
            endDate = endDate,
            organization = organization,
            backgroundImage = savedBackgroundImage,
        )

        organization.events.add(newEvent)
        eventRepository.save(newEvent)
        organizationRepository.save(organization)

        return newEvent
    }
}
