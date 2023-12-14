package pl.edu.agh.server.application.event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.annotation.AuthorizeEventAccess
import pl.edu.agh.server.domain.annotation.AuthorizeOrganizationAccess
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.dto.FullEventDTO
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventBelongToOrganization
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventFromFollowedByUser
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRange
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventInDateRangeType
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventNotCanceled
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventSavedByUser
import pl.edu.agh.server.domain.event.EventSpecification.Companion.eventWithNameLike
import pl.edu.agh.server.domain.event.EventSpecification.Companion.organizationNotArchived
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.foundation.application.BaseControllerUtilities
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val userService: UserService,
    private val jwtService: JwtService,
) : BaseControllerUtilities<Event>(jwtService) {

    @PostMapping("/save")
    fun saveEventForUser(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.saveEventForUser(getUserName(request), eventId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/remove")
    fun removeEventForUser(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.removeEventForUser(getUserName(request), eventId)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getFilteredEvents(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @RequestParam(name = "showCanceled", defaultValue = true.toString()) showCanceled: Boolean,
        @RequestParam(name = "savedOnly", defaultValue = false.toString()) savedOnly: Boolean,
        @RequestParam(name = "fromFollowedOnly", defaultValue = false.toString()) fromFollowedOnly: Boolean,
        @RequestParam(name = "showFromArchived", defaultValue = false.toString()) showFromArchived: Boolean,
        @RequestParam(name = "type", required = false) type: EventsType?,
        @RequestParam(name = "organizationId", required = false) organizationId: Long?,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date?,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date?,
        @RequestParam(name = "name", required = false) name: String?,
        request: HttpServletRequest,
    ): ResponseEntity<Page<EventDTO>> {
        val entities = getAllFilteredEventDTOs(
            page,
            size,
            sort,
            language,
            savedOnly,
            fromFollowedOnly,
            type,
            organizationId,
            startDate,
            endDate,
            name,
            showCanceled,
            showFromArchived,
            request,
        )
        return ResponseEntity.ok(
            entities,
        )
    }

    @GetMapping("/groupedByDate")
    fun getFilteredEventsGroupedByDate(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @RequestParam(name = "showCanceled", defaultValue = true.toString()) showCanceled: Boolean,
        @RequestParam(name = "savedOnly", defaultValue = false.toString()) savedOnly: Boolean,
        @RequestParam(name = "fromFollowedOnly", defaultValue = false.toString()) fromFollowedOnly: Boolean,
        @RequestParam(name = "showFromArchived", defaultValue = false.toString()) showFromArchived: Boolean,
        @RequestParam(name = "type", required = false) type: EventsType?,
        @RequestParam(name = "organizationId", required = false) organizationId: Long?,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date?,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date?,
        @RequestParam(name = "name", required = false) name: String?,
        request: HttpServletRequest,
    ): ResponseEntity<SortedMap<String, List<EventDTO>>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val entities = getAllFilteredEventDTOs(
            page,
            size,
            sort,
            language,
            savedOnly,
            fromFollowedOnly,
            type,
            organizationId,
            startDate,
            endDate,
            name,
            showCanceled,
            showFromArchived,
            request,
        ).content
        return ResponseEntity.ok(entities.groupBy { dateFormat.format(it.startDate) }.toSortedMap())
    }

    @PostMapping("/organization/{organizationId}")
    @AuthorizeOrganizationAccess(allowedRoles = ["HEAD", "CONTENT_CREATOR"])
    fun createEventForOrganization(
        request: HttpServletRequest,
        @PathVariable organizationId: Long,
        @ModelAttribute eventCreationRequest: EventCreationRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
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

        return ResponseEntity.ok(eventService.transformToEventDTO(event, language, getUserName(request)))
    }

    @PutMapping("/{eventId}")
    @AuthorizeEventAccess(allowedRoles = ["HEAD", "CONTENT_CREATOR"])
    fun updateEventForOrganization(
        request: HttpServletRequest,
        @PathVariable eventId: Long,
        @RequestBody eventUpdateRequest: EventUpdateRequest,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
    ): ResponseEntity<EventDTO> {
        val objectMapper = jacksonObjectMapper()
        val nameMap: Map<LanguageOption, String> = objectMapper.readValue(eventUpdateRequest.name)
        val descriptionMap: Map<LanguageOption, String> = objectMapper.readValue(eventUpdateRequest.description)
        val locationMap: Map<LanguageOption, String> = objectMapper.readValue(eventUpdateRequest.location)
        val startDate = Date(eventUpdateRequest.startDateTimestamp)
        val endDate = Date(eventUpdateRequest.endDateTimestamp)

        val event = eventService.updateEvent(
            eventId = eventId,
            backgroundImage = eventUpdateRequest.backgroundImage,
            nameMap = nameMap,
            descriptionMap = descriptionMap,
            locationMap = locationMap,
            startDate = startDate,
            endDate = endDate,
        )

        return ResponseEntity.ok(eventService.transformToEventDTO(event, language, getUserName(request)))
    }

    @GetMapping("/{id}")
    fun getEvent(
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @PathVariable id: Long,
        request: HttpServletRequest,
    ): ResponseEntity<EventDTO> {
        return ResponseEntity.ok(
            eventService.transformToEventDTO(
                eventService.getEvent(id),
                language,
                getUserName(request),
            ),
        )
    }

    @GetMapping("/{id}/full")
    fun getFullEvent(
        @PathVariable id: Long,
        request: HttpServletRequest,
    ): ResponseEntity<FullEventDTO> {
        return ResponseEntity.ok(eventService.transformToFullEventDTO(eventService.getEvent(id)))
    }

    @PostMapping("/cancel")
    @AuthorizeEventAccess(allowedRoles = ["HEAD", "CONTENT_CREATOR"])
    fun cancelEvent(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.cancelEvent(eventId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/reactivate")
    @AuthorizeEventAccess(allowedRoles = ["HEAD", "CONTENT_CREATOR"])
    fun reactivateEvent(
        request: HttpServletRequest,
        @RequestParam eventId: Long,
    ): ResponseEntity<Void> {
        eventService.reactivateEvent(eventId)
        return ResponseEntity.ok().build()
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
        name: String?,
        showCanceled: Boolean,
        showFromArchived: Boolean,
        request: HttpServletRequest,
    ): Page<EventDTO> {
        val userName = getUserName(request)
        val user = userService.getUserByEmail(userName)
        val pageRequest = createPageRequest(page, size, sort)

        val eventsPage = eventService.getAllWithSpecificationPageable(
            Specification.allOf(
                if (!showFromArchived) organizationNotArchived() else null,
                if (!showCanceled) eventNotCanceled() else null,
                if (savedOnly) eventSavedByUser(userName) else null,
                if (fromFollowedOnly) eventFromFollowedByUser(user) else null,
                if (type != null) {
                    eventInDateRangeType(
                        Date.from(Instant.now()),
                        type,
                    )
                } else {
                    null
                }, // TODO replace this with simple eventInDateRange
                if (organizationId != null) eventBelongToOrganization(organizationId) else null,
                if (startDate != null && endDate != null) eventInDateRange(startDate, endDate) else null,
                if (name != null) eventWithNameLike(name, language) else null,
            ),
            pageRequest,
        )

        return PageImpl(
            eventService.transformToEventDTO(
                eventsPage.content,
                language,
                getUserName(request),
            ),
            pageRequest,
            eventsPage.totalElements,
        )
    }

    //    FIXME use function from base controller once NullPointerException is fixed
    override fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
