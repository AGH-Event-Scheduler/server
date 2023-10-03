package pl.edu.agh.server.domain.event

import org.springframework.stereotype.Service

@Service
class EventService(private val eventRepository: EventRepository)
