package pl.edu.agh.server.domain.annotation

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRoleRepository

@Aspect
@Component
class AuthorizationAspect(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val organizationUserRoleRepository: OrganizationUserRoleRepository,
) {

    @Before("@annotation(authorizeAccess) && args(request, organizationId, ..)")
    fun beforeMethodExecution(
        joinPoint: JoinPoint,
        authorizeAccess: AuthorizeAccess,
        request: HttpServletRequest,
        organizationId: Long,
    ) {
        val userId = userService.getUserIdByEmail(getUserName(request))
        val userAuthorities = organizationUserRoleRepository.findByOrganizationIdAndUserId(organizationId, userId)
        val hasAuthority = userAuthorities.stream().anyMatch { it.role.name in authorizeAccess.allowedRoles }
        hasAuthority || throw AuthorizationException("User does not have required authority")
    }

    fun getUserName(request: HttpServletRequest): String {
        try {
            return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
        } catch (e: Exception) {
            throw AuthorizationException("JWT Authorization Problem")
        }
    }
}
