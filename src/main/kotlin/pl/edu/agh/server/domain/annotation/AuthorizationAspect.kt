package pl.edu.agh.server.domain.annotation

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRoleRepository

@Aspect
@Component
class AuthorizationAspect(
    private val userService: UserService,
    private val organizationUserRoleRepository: OrganizationUserRoleRepository,
    private val eventRepository: EventRepository,
) {

    @Before("@annotation(authorizeOrganizationAccess) && args(request, organizationId, ..)")
    fun beforeOrganizationMethodExecution(
        joinPoint: JoinPoint,
        authorizeOrganizationAccess: AuthorizeOrganizationAccess,
        request: HttpServletRequest,
        organizationId: Long,
    ) {
        if (userService.isAdmin(request)) return
        val userAuthorities =
            organizationUserRoleRepository.findByOrganizationIdAndUserId(organizationId, userService.getUserId(request))
        userAuthorities.stream()
            .anyMatch { it.role.name in authorizeOrganizationAccess.allowedRoles } || throw AuthorizationException("User does not have required authority")
    }

    @Before("@annotation(authorizeEventAccess) && args(request, eventId, ..)")
    fun beforeEventMethodExecution(
        joinPoint: JoinPoint,
        authorizeEventAccess: AuthorizeEventAccess,
        request: HttpServletRequest,
        eventId: Long,
    ) {
        if (userService.isAdmin(request)) return
        val organizationId = eventRepository.findOrganizationIdById(eventId)
            .orElseThrow { throw AuthorizationException("Event does not exist") }
        val userAuthorities =
            organizationUserRoleRepository.findByOrganizationIdAndUserId(organizationId, userService.getUserId(request))
        userAuthorities.stream()
            .anyMatch { it.role.name in authorizeEventAccess.allowedRoles } || throw AuthorizationException("User does not have required authority")
    }

    @Before("@annotation(adminRestricted) && args(request, ..)")
    fun beforeAdminAuthorizedMethod(
        joinPoint: JoinPoint,
        adminRestricted: AdminRestricted,
        request: HttpServletRequest,
    ) {
        if (userService.isAdmin(request)) return
        throw AuthorizationException("User does not have required authority")
    }
}
