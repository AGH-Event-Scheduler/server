package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageService.IncorrectFileUploadException
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.translation.TranslationService
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRole
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRoleRepository
import pl.edu.agh.server.foundation.application.BaseServiceUtilities
import java.util.*

@Service
class OrganizationService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val modelMapper: ModelMapper,
    private val userOrganizationRoleRepository: OrganizationUserRoleRepository,
    private val translationService: TranslationService,
    private val imageService: ImageService,
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

    @Transactional
    fun createOrganization(
        logoImageFile: MultipartFile?,
        backgroundImageFile: MultipartFile?,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
    ): Organization {
        val backgroundImage: BackgroundImage
        val logoImage: LogoImage

        if (backgroundImageFile != null) {
            backgroundImage = imageService.createBackgroundImage(backgroundImageFile)
        } else {
            throw IncorrectFileUploadException("Uploaded file does not exist")
        }

        if (logoImageFile != null) {
            logoImage = imageService.createLogoImage(logoImageFile)
        } else {
            throw IncorrectFileUploadException("Uploaded file does not exist")
        }

        val newOrganization = Organization(
            name = translationService.createTranslation(nameMap),
            description = translationService.createTranslation(descriptionMap),
            backgroundImage = backgroundImage,
            logoImage = logoImage,
        )
        return organizationRepository.save(newOrganization)
    } //    FIXME use function from base service once NullPointerException is fixed

    override fun getAllWithSpecificationPageable(
        specification: Specification<Organization>,
        pageable: PageRequest,
    ): List<Organization> {
        return organizationRepository.findAll(specification, pageable).content
    }

    fun transformToOrganizationDTO(
        organizations: List<Organization>,
        language: LanguageOption,
        userName: String? = null,
    ): List<OrganizationDTO> {
        val user: Optional<User> = userName?.let { userRepository.findByEmail(it) } ?: Optional.empty()
        return organizations.map {
            modelMapper.map(it, OrganizationDTO::class.java).apply {
                isSubscribed = (user.isPresent) && user.get().followedOrganizations.contains(it)
                description = getTranslatedContent(it.description, language)
                name = getTranslatedContent(it.name, language)
            }
        }
    }

    private fun getTranslatedContent(translations: Set<Translation>, language: LanguageOption): String {
        return translations.firstOrNull { translation -> translation.language === language }?.content ?: ""
    }

    fun transformToOrganizationDTO(
        organization: Organization,
        language: LanguageOption,
        userName: String? = null,
    ): OrganizationDTO {
        return transformToOrganizationDTO(listOf(organization), language, userName).first()
    }

    @Transactional
    fun assignUserRole(organizationId: Long, userId: Long, role: OrganizationRole) {
        if (!userOrganizationRoleRepository.existsByOrganizationIdAndUserIdAndRole(organizationId, userId, role)) {
            val organization = organizationRepository.findById(organizationId).orElseThrow()
            val user = userRepository.findById(userId).orElseThrow()

            val newAssignment = OrganizationUserRole(
                user = user,
                organization = organization,
                role = role,
            )

            userOrganizationRoleRepository.save(newAssignment)
        }
    }
}
