package pl.edu.agh.server.domain.event

import org.modelmapper.ModelMapper
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.exception.EventNotFoundException
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService

@Service
class UserEventService(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val userService: UserService,
    private val modelMapper: ModelMapper,
) {

    @Transactional
    fun saveEventForUser(userName: String, eventId: Long): User {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val event = eventRepository.findById(eventId)
            .orElseThrow { throw EventNotFoundException(eventId) }

        user.savedEvents.add(event)
        return userRepository.save(user)
    }

    @Transactional
    fun removeEventForUser(userName: String, organizationId: Long): User {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val event = eventRepository.findById(organizationId)
            .orElseThrow { throw EventNotFoundException(organizationId) }

        user.savedEvents.remove(event)
        return userRepository.save(user)
    }

    fun getAllEventsWithStatusByUserWithSpecification(userName: String?, specification: Specification<Event>? = null): List<EventDTO> {
        val allEvents = eventRepository.findAll(specification)
        val savedEvents = userName?.let { userService.getEventsSavedByUser(it) } ?: mutableSetOf()
        return allEvents.map {
            val eventDto = modelMapper.map(it, EventDTO::class.java)
            eventDto.isSaved = savedEvents.contains(it)
            eventDto
        }
    }

    fun getSavedEventsByUser(userName: String): List<EventDTO> {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        return user.savedEvents.map {
            modelMapper.map(it, EventDTO::class.java).apply { isSaved = true }
        }
    }

    fun getEventById(eventId: Long, userName: String?): EventDTO {
        val event = eventRepository.findById(eventId)
            .orElseThrow { throw OrganizationNotFoundException(eventId) }

        val savedEvents = userName?.let { userService.getEventsSavedByUser(it) } ?: mutableSetOf()
        val eventDto = modelMapper.map(event, EventDTO::class.java).apply {
            isSaved = savedEvents.contains(event)
        }

        return eventDto
    }
}
