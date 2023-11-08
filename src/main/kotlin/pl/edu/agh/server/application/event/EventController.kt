package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventBelongToOrganization
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventFromFollowedByUser
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRange
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRangeType
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventSavedByUser
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

    @PostMapping("/save")
    fun subscribeUserToOrganization(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.saveEventForUser(getUserName(request), eventId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/remove")
    fun unsubscribeUserFromOrganization(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.saveEventForUser(getUserName(request), eventId)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getEventsInDateRange(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @RequestParam(name = "savedOnly", defaultValue = false.toString()) savedOnly: Boolean,
        @RequestParam(name = "fromFollowedOnly", defaultValue = false.toString()) fromFollowedOnly: Boolean,
        @RequestParam(name = "type", required = false) type: EventsType?,
        @RequestParam(name = "organizationId", required = false) organizationId: Long?,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date?,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date?,
        request: HttpServletRequest,
    ): ResponseEntity<List<EventDTO>> {
        val entities = getAllFilteredEventDTOs(page, size, sort, language, savedOnly, fromFollowedOnly, type, organizationId, startDate, endDate, request)
        return ResponseEntity.ok(
            entities,
        )
    }

    @GetMapping("/groupedByDate")
    fun getEventsInDateRangeGroupedByDate(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @RequestParam(name = "savedOnly", defaultValue = false.toString()) savedOnly: Boolean,
        @RequestParam(name = "fromFollowedOnly", defaultValue = false.toString()) fromFollowedOnly: Boolean,
        @RequestParam(name = "type", required = false) type: EventsType?,
        @RequestParam(name = "organizationId", required = false) organizationId: Long?,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date?,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date?,
        request: HttpServletRequest,
    ): ResponseEntity<SortedMap<String, List<EventDTO>>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val entities = getAllFilteredEventDTOs(page, size, sort, language, savedOnly, fromFollowedOnly, type, organizationId, startDate, endDate, request)
        return ResponseEntity.ok(entities.groupBy { dateFormat.format(it.startDate) }.toSortedMap())
    }

    @PostMapping("/organization/{organizationId}")
    fun createEventForOrganization(
        @PathVariable organizationId: Long,
        @RequestBody eventCreationRequest: EventCreationRequest,
        request: HttpServletRequest,
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

        return ResponseEntity.ok(eventService.transformToEventDTO(event, LanguageOption.PL, getUserName(request)).orElseThrow { RuntimeException() })
    }

    @GetMapping("/{id}")
    fun getEvent(
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable id: Long,
        request: HttpServletRequest,
    ): ResponseEntity<EventDTO> {
        return ResponseEntity.ok(eventService.transformToEventDTO(eventService.getEvent(id), language, getUserName(request)).orElseThrow { throw EventNotFoundException(id) })
    }

    private fun getAllFilteredEventDTOs(
        page: Int,
        size: Int,
        sort: String,
        language: LanguageOption,
        savedOnly: Boolean,
        fromFollowedOnly: Boolean,
        type: EventsType?,
        organizationId: Long?,
        startDate: Date?,
        endDate: Date?,
        request: HttpServletRequest,
    ): List<EventDTO> {
        val userName = getUserName(request)

        return eventService.transformToEventDTO(
            eventService.getAllWithSpecificationPageable(
                Specification.allOf(
                    if (savedOnly) eventSavedByUser(userName) else null,
                    if (fromFollowedOnly) eventFromFollowedByUser(userName) else null,
                    if (type != null) eventInDateRangeType(Date.from(Instant.now()), type) else null, // TODO replace this with simple eventInDateRange
                    if (organizationId != null) eventBelongToOrganization(organizationId) else null,
                    if (startDate != null && endDate != null) eventInDateRange(startDate, endDate) else null,
                ),
                createPageRequest(page, size, sort),
            ),
            language,
        )
    }

//    FIXME use function from base controller once NullPointerException is fixed
    override fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
