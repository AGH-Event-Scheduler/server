package pl.edu.agh.server.domain.event

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventFromOrganizationAndInDateRange
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.organization.OrganizationRepository
import java.time.Instant
import java.util.*

@Service
class EventService(private val eventRepository: EventRepository, private val organizationRepository: OrganizationRepository) {
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
}
