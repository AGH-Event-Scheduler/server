package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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

    @PostMapping("/organization/{id}")
    fun createEventForOrganization(
        @PathVariable id: Long,
        @RequestParam("backgroundImage") backgroundImage: MultipartFile,
        @RequestParam("name") name: String,
        @RequestParam("description") description: String,
        @RequestParam("location") location: String,
        @RequestParam("startDate") startDateTimestamp: Long,
        @RequestParam("endDate") endDateTimestamp: Long,
    ): ResponseEntity<String> {
        val objectMapper = jacksonObjectMapper()
        val nameDictionary = objectMapper.readValue(name, Map::class.java)
        val descriptionDictionary = objectMapper.readValue(description, Map::class.java)
        val locationDictionary = objectMapper.readValue(location, Map::class.java)
        val startDate = Date(startDateTimestamp)
        val endDate = Date(endDateTimestamp)

        println(id)
        println(backgroundImage)
        println(nameDictionary)
        println(descriptionDictionary)
        println(locationDictionary)
        println(startDate)
        println(endDate)

        eventService.createEvent(backgroundImage = backgroundImage)

        return ResponseEntity.ok("{\"message\":\"OK\"}")
    }

    class EventCreationException(s: String) : RuntimeException(s)
}
