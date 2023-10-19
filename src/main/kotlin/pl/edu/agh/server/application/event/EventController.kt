package pl.edu.agh.server.application.event

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventBelongToOrganization
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
        @PathVariable id: Long,
    ): ResponseEntity<List<Event>> {
        return respondWithAllWithSpecification(page, size, sort, eventBelongToOrganization(id))
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
}