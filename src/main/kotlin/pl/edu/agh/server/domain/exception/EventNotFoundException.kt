package pl.edu.agh.server.domain.exception

class EventNotFoundException(eventId: Long) :
    RuntimeException("Event with ID $eventId not found")
