package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.agh.server.domain.dto.OrganizationDto
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService

@Service
class UserOrganizationService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val userService: UserService,
    private val modelMapper: ModelMapper,
) {

    @Transactional
    fun subscribeUserToOrganization(userName: String, organizationId: Long): User {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }

        user.organizations.add(organization)
        return userRepository.save(user)
    }

    @Transactional
    fun unsubscribeUserFromOrganization(userName: String, organizationId: Long): User {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }

        user.organizations.remove(organization)
        return userRepository.save(user)
    }

    fun getAllOrganizationsWithStatusByUser(userName: String?): List<OrganizationDto> {
        val allOrganizations = organizationRepository.findAll()
        return allOrganizations.map {
            val organizationDto = modelMapper.map(it, OrganizationDto::class.java)
            if (userName != null) {
                organizationDto.isSubscribed = userService.isUserSubscribedToOrganization(userName, it.id!!)
            }
            organizationDto
        }
    }

    fun getSubscribedOrganizationsByUser(userName: String): List<OrganizationDto> {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        return user.organizations.map {
            modelMapper.map(it, OrganizationDto::class.java).apply { isSubscribed = true }
        }
    }

    fun getOrganizationById(organizationId: Long, userName: String?): OrganizationDto {
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }

        val organizationDto = modelMapper.map(organization, OrganizationDto::class.java)

        if (userName != null) {
            organizationDto.isSubscribed = userService.isUserSubscribedToOrganization(userName, organizationId)
        }

        return organizationDto
    }
}
