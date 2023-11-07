package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.TranslationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.TranslationService
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService
import java.util.*

@Service
class UserOrganizationService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val userService: UserService,
    private val translationService: TranslationService,
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

    fun getAllOrganizationsWithStatusByUser(userName: String?, language: LanguageOption): List<OrganizationDTO> {
        val allOrganizations = organizationRepository.findAll()
        val organizationDTOs = getWithTranslations(allOrganizations, language)
        return organizationDTOs.map {
            if (userName != null) {
                it.isSubscribed = userService.isUserSubscribedToOrganization(userName, it.id!!)
            }
            it
        }
    }

    fun getSubscribedOrganizationsByUser(userName: String, language: LanguageOption): List<OrganizationDTO> {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val organizationDTOs = getWithTranslations(user.organizations.toList(), language)
        return organizationDTOs.map {
            it.isSubscribed = true
            it
        }
    }

    fun getOrganizationById(organizationId: Long, userName: String?, language: LanguageOption): OrganizationDTO {
        val organization = organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }
        val organizationDTO = getWithTranslations(organization, language).orElseThrow { TranslationNotFoundException() }

        if (userName != null) {
            organizationDTO.isSubscribed = userService.isUserSubscribedToOrganization(userName, organizationId)
        }

        return organizationDTO
    }

    private fun getWithTranslations(organizations: List<Organization>, language: LanguageOption): List<OrganizationDTO> {
        val translationIds = mutableListOf<UUID>()
        organizations.forEach {
            translationIds.add(it.name)
            translationIds.add(it.description)
        }

        val translations = translationService.getTranslations(translationIds, language)
        val translationsMap = translations.associateBy({ it.translationId }, { it.content })

        val organizationDTOs = organizations.map {
            modelMapper.map(it, OrganizationDTO::class.java)
                .apply {
                    name = translationsMap[it.name] ?: ""
                    description = translationsMap[it.description] ?: ""
                }
        }

        return organizationDTOs
    }

    private fun getWithTranslations(organization: Organization, language: LanguageOption): Optional<OrganizationDTO> {
        return Optional.ofNullable(getWithTranslations(listOf(organization), language).firstOrNull())
    }
}
