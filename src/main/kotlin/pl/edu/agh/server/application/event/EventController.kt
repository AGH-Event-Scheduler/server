package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventBelongToOrganization
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRange
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRangeType
import pl.edu.agh.server.domain.exception.EventNotFoundException
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.foundation.application.BaseControllerUtilities
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val jwtService: JwtService,
) : BaseControllerUtilities<Event>(jwtService) {

    @GetMapping("/organization/{organizationId}")
    fun getOrganizationEvents(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "type", defaultValue = "UPCOMING") type: EventsType,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable organizationId: Long,
    ): ResponseEntity<List<EventDTO>> {
        val events = eventService.transformToEventDTO(
            eventService
                .getAllWithSpecificationPageable(eventInDateRangeType(Date.from(Instant.now()), type).and(eventBelongToOrganization(organizationId)), createPageRequest(page, size, sort)),
            language,
            null,
        )
        return ResponseEntity.ok(events)
    }

    @GetMapping("/byDate")
    fun getEventsInDateRange(
        @RequestParam(name = "page", defaultValue = "0") page: Int, // TODO replace page, size, sort with object
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "startDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date,
        @RequestParam(name = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
    ): ResponseEntity<SortedMap<String, List<EventDTO>>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val groupedEntities: SortedMap<String, List<EventDTO>> = eventService.transformToEventDTO(eventService.getAllWithSpecificationPageable(eventInDateRange(startDate, endDate), createPageRequest(page, size, sort)), language, null)
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
    ): ResponseEntity<EventDTO> {
        val objectMapper = jacksonObjectMapper()
        val nameMap: Map<LanguageOption, String> = objectMapper.readValue(eventCreationRequest.name)
        val descriptionMap: Map<LanguageOption, String> = objectMapper.readValue(eventCreationRequest.description)
        val locationMap: Map<LanguageOption, String> = objectMapper.readValue(eventCreationRequest.location)
        val startDate = Date(eventCreationRequest.startDateTimestamp)
        val endDate = Date(eventCreationRequest.endDateTimestamp)

        val event = eventService.createEvent(
            organizationId = organizationId,
            backgroundImage = eventCreationRequest.backgroundImage,
            nameMap = nameMap,
            descriptionMap = descriptionMap,
            locationMap = locationMap,
            startDate = startDate,
            endDate = endDate,
        )

        return ResponseEntity.ok(eventService.transformToEventDTO(event, LanguageOption.PL, null).orElseThrow { RuntimeException() })
    }

    @GetMapping("/{id}")
    fun getEvent(
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable id: Long,
    ): ResponseEntity<EventDTO> {
        return ResponseEntity.ok(eventService.transformToEventDTO(eventService.getEvent(id), language, null).orElseThrow { throw EventNotFoundException(id) })
    }
}
