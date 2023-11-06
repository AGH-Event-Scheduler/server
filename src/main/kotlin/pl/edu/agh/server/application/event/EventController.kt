package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.translation.LanguageOption
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
) {

    @GetMapping("/organization/{id}")
    fun getOrganizationEvents(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "type", defaultValue = "UPCOMING") type: EventsType,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable id: Long,
    ): ResponseEntity<List<EventDTO>> {
        val events = eventService
            .getAllFromOrganizationInDateRange(page, size, sort, id, type, language)
        return ResponseEntity.ok(events)
    }

    @GetMapping("/byDate")
    fun getEventsInDateRange(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,asc") sort: String,
        @RequestParam(name = "startDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date,
        @RequestParam(name = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
    ): ResponseEntity<SortedMap<String, List<EventDTO>>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val groupedEntities: SortedMap<String, List<EventDTO>> =
            eventService.getAllInDateRange(page, size, sort, startDate, endDate, language)
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

        return ResponseEntity.ok(event)
    }

    @GetMapping("/{id}")
    fun getEvent(
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable id: Long,
    ): ResponseEntity<EventDTO> {
        val event = eventService.getEvent(id, language)
        return if (event.isPresent) {
            ResponseEntity.ok(event.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
