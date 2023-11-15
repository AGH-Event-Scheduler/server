package pl.edu.agh.server.application.notification

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.NotificationDTO
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.notification.NotificationService
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.foundation.application.BaseControllerUtilities

@RestController
@RequestMapping("/api/feed")
class NotificationController(
    private val notificationService: NotificationService,
    private val jwtService: JwtService,
) : BaseControllerUtilities<Event>(jwtService) {

    @GetMapping
    fun getFeedForUser(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
        @RequestParam(name = "sort", defaultValue = "startDate,desc") sort: String,
        @RequestParam(name = "language", defaultValue = "PL") language: LanguageOption,
        request: HttpServletRequest,
    ): List<NotificationDTO> {
//        TODO add support for listing only not seen ones
        val notifications = notificationService.getAllWithSpecificationPageable(
            Specification.anyOf(
                null,
            ),
            createPageRequest(page, size, sort),
        )
        return notificationService.transformToNotificationDTO(notifications, language, getUserName(request))
    }

//    FIXME use function from base controller once NullPointerException is fixed
    override fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
