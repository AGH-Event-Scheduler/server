package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.agh.server.domain.dto.OrganizationDto
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService
import java.util.*

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

        user.followedOrganizations.add(organization)
        return userRepository.save(user)
    }

    @Transactional
    fun unsubscribeUserFromOrganization(userName: String, organizationId: Long): User {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }

        user.followedOrganizations.remove(organization)
        return userRepository.save(user)
    }

    fun getAllOrganizationsWithStatusByUserWithSpecification(userName: String?, specification: Specification<Organization>? = null): List<OrganizationDto> {
        val allOrganizations = organizationRepository.findAll(specification)
        val followedOrganizations = userName?.let { userService.getSubscribedOrganizationsByUser(it) } ?: mutableSetOf()
        return allOrganizations.map {
            val organizationDto = modelMapper.map(it, OrganizationDto::class.java)
            organizationDto.isSubscribed = followedOrganizations.contains(it)
            organizationDto
        }
    }

    fun getSubscribedOrganizationsByUser(userName: String): List<OrganizationDto> {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        return user.followedOrganizations.map {
            modelMapper.map(it, OrganizationDto::class.java).apply { isSubscribed = true }
        }
    }

    fun getOrganizationById(organizationId: Long, userName: String?): OrganizationDto {
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }

        val followedOrganizations = userName?.let { userService.getSubscribedOrganizationsByUser(it) } ?: mutableSetOf()
        val organizationDto = modelMapper.map(organization, OrganizationDto::class.java).apply {
            isSubscribed = followedOrganizations.contains(organization)
        }

        return organizationDto
    }

    fun transformToOrganizationDTO(organizations: List<Organization>, language: LanguageOption, userName: String? = null): List<OrganizationDto> {
//        TODO: implement once translations are done
        return organizations.map { modelMapper.map(it, OrganizationDto::class.java) }
    }

    fun transformToOrganizationDTO(organization: Organization, language: LanguageOption, userName: String? = null): Optional<OrganizationDto> {
        return Optional.ofNullable(transformToOrganizationDTO(listOf(organization), language, userName).firstOrNull())
    }
}
