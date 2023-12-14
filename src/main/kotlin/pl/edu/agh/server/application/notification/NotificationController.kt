package pl.edu.agh.server.application.notification

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.NotificationDTO
import pl.edu.agh.server.domain.notification.Notification
import pl.edu.agh.server.domain.notification.NotificationDTOTranslateService
import pl.edu.agh.server.domain.notification.NotificationService
import pl.edu.agh.server.domain.notification.NotificationSpecification.Companion.notificationForAdmins
import pl.edu.agh.server.domain.notification.NotificationSpecification.Companion.notificationForAllUsers
import pl.edu.agh.server.domain.notification.NotificationSpecification.Companion.notificationForUserFollowingOrganization
import pl.edu.agh.server.domain.notification.NotificationSpecification.Companion.notificationForUserWithSavedEvents
import pl.edu.agh.server.domain.notification.NotificationSpecification.Companion.notificationNotSeenByUser
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.foundation.application.BaseControllerUtilities

@RestController
@RequestMapping("/api/feed")
class NotificationController(
    private val notificationService: NotificationService,
    private val notificationDTOTranslateService: NotificationDTOTranslateService,
    private val jwtService: JwtService,
    private val userService: UserService,
) : BaseControllerUtilities<Notification>(jwtService) {

    @GetMapping
    fun getFeedForUser(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "creationDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        @RequestParam(name = "showNotSeenOnly", defaultValue = false.toString()) showNotSeenOnly: Boolean,
        request: HttpServletRequest,
    ): ResponseEntity<Page<NotificationDTO>> {
        val user = userService.getUserByEmail(getUserName(request))
        val pageRequest = createPageRequest(page, size, sort)
        val notificationsPage = notificationService.getAllWithSpecificationPageable(
            Specification.allOf(
                if (showNotSeenOnly) notificationNotSeenByUser(user) else null,
                Specification.anyOf(
                    notificationForAllUsers(),
                    notificationForAdmins(user),
                    notificationForUserWithSavedEvents(user),
                    notificationForUserFollowingOrganization(user),
                ),
            ),
            pageRequest,
        )
        return ResponseEntity.ok(
            PageImpl(
                notificationDTOTranslateService.transformToNotificationDTO(notificationsPage.content, language, user),
                pageRequest,
                notificationsPage.totalElements,
            ),
        )
    }

    @PostMapping("/{notificationId}")
    fun markNotificationAsSeen(
        @PathVariable notificationId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        notificationService.markNotificationAsSeen(getUserName(request), notificationId)
        return ResponseEntity.ok(null)
    }

//    FIXME use function from base controller once NullPointerException is fixed
    override fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
