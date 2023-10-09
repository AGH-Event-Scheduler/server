package pl.edu.agh.server.application.event

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventBelongToOrganization
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController

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
        @RequestParam(name = "sort", defaultValue = "startDate,asc") sort: String,
        @PathVariable id: Long,
    ): ResponseEntity<List<Event>> {
        return getAllWithSpecification(page, size, sort, eventBelongToOrganization(id))
    }
}
