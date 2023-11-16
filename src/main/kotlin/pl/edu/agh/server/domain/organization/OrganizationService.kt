package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.TranslationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.foundation.application.BaseServiceUtilities
import java.util.*

@Service
class OrganizationService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val userService: UserService,
    private val translationService: TranslationService,
    private val modelMapper: ModelMapper,
) : BaseServiceUtilities<Organization>(organizationRepository) {

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

    fun getOrganization(organizationId: Long, userName: String?): Organization {
        return organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }
    }

//    FIXME use function from base service once NullPointerException is fixed
    override fun getAllWithSpecificationPageable(specification: Specification<Organization>, pageable: PageRequest): List<Organization> {
        return organizationRepository.findAll(specification, pageable).content
    }

    fun transformToOrganizationDTO(organizations: List<Organization>, language: LanguageOption, userName: String? = null): List<OrganizationDto> {
//        TODO: implement once translations are done
        val user: Optional<User> = userName?.let { userRepository.findByEmail(it) } ?: Optional.empty()
        return organizations.map {
            modelMapper.map(it, OrganizationDto::class.java).apply {
                isSubscribed = (user.isPresent) && user.get().followedOrganizations.contains(it)
            }
        }
    }

    fun transformToOrganizationDTO(organization: Organization, language: LanguageOption, userName: String? = null): OrganizationDto {
        return transformToOrganizationDTO(listOf(organization), language, userName).first()
    }
}
