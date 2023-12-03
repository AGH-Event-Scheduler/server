package pl.edu.agh.server.domain.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.annotation.AuthorizationException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRoleRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val organizationUserRoleRepository: OrganizationUserRoleRepository,
) {
    fun checkUserExist(userName: String): Boolean {
        return userRepository.existsByEmail(userName)
    }

    fun getUserByEmail(userName: String): User {
        return userRepository.findByEmail(userName).orElseThrow { UserNotFoundException(userName) }
    }

    fun getUserIdByEmail(userName: String): Long {
        return userRepository.findIdByEmail(userName).orElseThrow { UserNotFoundException(userName) }
    }

    fun getOrganizationRoles(organizationId: Long, userId: Long): List<OrganizationRole> {
        return organizationUserRoleRepository.findOrganizationRoleByOrganizationIdAndUserId(organizationId, userId)
    }

    fun getOrganizationRoles(organizationId: Long, email: String): List<OrganizationRole> {
        return organizationUserRoleRepository.findOrganizationRoleByOrganizationIdAndUserId(
            organizationId,
            getUserIdByEmail(email),
        )
    }

    fun getUserId(request: HttpServletRequest): Long {
        try {
            return getUserIdByEmail(jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7)))
        } catch (e: Exception) {
            throw AuthorizationException("JWT Authorization Problem")
        }
    }

    fun isAdmin(request: HttpServletRequest): Boolean {
        val username = jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
        val user = userRepository.findByEmail(username).orElseThrow { UserNotFoundException(username) }
        return user.role == Role.ADMIN
    }

    fun hasAnyRoleAssigned(request: HttpServletRequest): Boolean? {
        return isAdmin(request) || organizationUserRoleRepository.existsByUserId(getUserId(request))
    }
}
