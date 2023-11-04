package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventInDateRange
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.SortedMap

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val eventRepository: EventRepository,
) : BaseIdentifiableCrudController<Event>(eventRepository) {

    @GetMapping("/organization/{id}")
    fun getOrganizationEvents(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "type", defaultValue = "UPCOMING") type: EventsType,
        @PathVariable id: Long,
    ): ResponseEntity<List<Event>> {
        val events = eventService
            .getAllFromOrganizationInDateRange(page, size, sort, id, type)
        return ResponseEntity.ok(events)
    }

    @GetMapping("/byDate")
    fun getEventsInDateRange(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,asc") sort: String,
        @RequestParam(name = "startDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date,
        @RequestParam(name = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date,
    ): ResponseEntity<SortedMap<String, List<Event>>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val groupedEntities: SortedMap<String, List<Event>> = getAllWithSpecification(page, size, sort, eventInDateRange(startDate, endDate))
            .groupBy { dateFormat.format(it.startDate) }
            .toSortedMap()
        return ResponseEntity.ok(
            groupedEntities,
        )
    }

    @PostMapping("/organization/{organizationId}")
    fun createEventForOrganization(
        @PathVariable organizationId: Long,
        @RequestBody eventCreationRequest: EventCreationRequest,
    ): ResponseEntity<Event> {
        val objectMapper = jacksonObjectMapper()
        val nameDictionary = objectMapper.readValue(eventCreationRequest.name, Map::class.java)
        val descriptionDictionary = objectMapper.readValue(eventCreationRequest.description, Map::class.java)
        val locationDictionary = objectMapper.readValue(eventCreationRequest.location, Map::class.java)
        val startDate = Date(eventCreationRequest.startDateTimestamp)
        val endDate = Date(eventCreationRequest.endDateTimestamp)

//        TODO pass multi language text
        val event = eventService.createEvent(
            organizationId = organizationId,
            backgroundImage = eventCreationRequest.backgroundImage,
            name = nameDictionary["pl"].toString(),
            description = descriptionDictionary["pl"].toString(),
            location = locationDictionary["pl"].toString(),
            startDate = startDate,
            endDate = endDate,
        )

        return ResponseEntity.ok(event)
    }
}
