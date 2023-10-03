package pl.edu.agh.server.application.event

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val eventRepository: EventRepository,
) : BaseIdentifiableCrudController<Event>(eventRepository)
