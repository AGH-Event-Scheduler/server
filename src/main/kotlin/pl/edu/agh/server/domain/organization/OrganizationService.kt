package pl.edu.agh.server.domain.organization

import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.domain.dto.FullOrganizationDTO
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageService.IncorrectFileUploadException
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.notification.NotificationService
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.translation.TranslationService
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.UserService
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
    private val userService: UserService,
    private val notificationService: NotificationService,
) : BaseServiceUtilities<Organization>(organizationRepository) {

    @Transactional
    fun archiveOrganization(organizationId: Long) {
        val organization = organizationRepository.findById(organizationId).orElseThrow { throw OrganizationNotFoundException(organizationId) }
        organization.isArchived = true
        val upDatedOrganization = organizationRepository.save(organization)
        notificationService.notifyAboutOrganizationArchive(upDatedOrganization)
    }

    @Transactional
    fun reactivateOrganization(organizationId: Long) {
        val organization = organizationRepository.findById(organizationId).orElseThrow { throw OrganizationNotFoundException(organizationId) }
        organization.isArchived = false
        val upDatedOrganization = organizationRepository.save(organization)
        notificationService.notifyAboutOrganizationReactivate(upDatedOrganization)
    }

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

    fun getOrganization(organizationId: Long): Organization {
        return organizationRepository.findById(organizationId)
            .orElseThrow { throw OrganizationNotFoundException(organizationId) }
    }

    @Transactional
    fun createOrganization(
        logoImageFile: MultipartFile?,
        backgroundImageFile: MultipartFile?,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
        leaderEmail: String,
    ): Organization {
        var backgroundImage: BackgroundImage? = null
        var logoImage: LogoImage? = null
        try {
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
            val organization = organizationRepository.save(newOrganization)

            val user = userService.getUserByEmail(leaderEmail)
            assignUserRole(organization.id!!, user.id!!, OrganizationRole.HEAD)

            notificationService.notifyAboutOrganizationCreation(organization)

            return organization
        } catch (e: RuntimeException) {
            if (backgroundImage != null) {
                imageService.removeImage(backgroundImage.imageId)
            }
            if (logoImage != null) {
                imageService.removeImage(logoImage.imageId)
            }
            throw e
        }
    }

    fun updateOrganization(
        organizationId: Long,
        logoImageFile: MultipartFile?,
        backgroundImageFile: MultipartFile?,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
    ): Organization {
        var savedBackgroundImage: BackgroundImage? = null
        var savedLogoImage: LogoImage? = null
        try {
            val organization = organizationRepository.findById(organizationId).orElseThrow { throw OrganizationNotFoundException(organizationId) }

            if (backgroundImageFile != null) {
                savedBackgroundImage = imageService.createBackgroundImage(backgroundImageFile)
                organization.backgroundImage = savedBackgroundImage
            }

            if (logoImageFile != null) {
                savedLogoImage = imageService.createLogoImage(logoImageFile)
                organization.logoImage = savedLogoImage
            }

            organization.name = translationService.createTranslation(nameMap)
            organization.description = translationService.createTranslation(descriptionMap)

            val savedOrganization = organizationRepository.save(organization)

            notificationService.notifyAboutOrganizationUpdate(savedOrganization)

            return savedOrganization
        } catch (e: RuntimeException) {
            if (savedBackgroundImage != null) {
                imageService.removeImage(savedBackgroundImage.imageId)
            }
            if (savedLogoImage != null) {
                imageService.removeImage(savedLogoImage.imageId)
            }
            throw e
        }
    }
    override fun getAllWithSpecificationPageable(
        specification: Specification<Organization>,
        pageable: PageRequest,
    ): Page<Organization> {
        return organizationRepository.findAll(specification, pageable)
    }

    fun transformToOrganizationDTO(
        organization: Organization,
        language: LanguageOption,
        userName: String? = null,
    ): OrganizationDTO {
        return transformToOrganizationDTO(listOf(organization), language, userName).first()
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

    fun transformToFullOrganizationDTO(organizations: List<Organization>): List<FullOrganizationDTO> {
        val fullOrganizationDTOs = organizations.map {
            modelMapper.map(it, FullOrganizationDTO::class.java)
                .apply {
                    nameMap = getTranslationMap(it.name)
                    descriptionMap = getTranslationMap(it.description)
                }
        }

        return fullOrganizationDTOs
    }

    fun transformToFullOrganizationDTO(organization: Organization): FullOrganizationDTO {
        return transformToFullOrganizationDTO(listOf(organization)).first()
    }

    private fun getTranslatedContent(translations: Set<Translation>, language: LanguageOption): String {
        return translations.firstOrNull { translation -> translation.language === language }?.content ?: ""
    }

    private fun getTranslationMap(translations: Set<Translation>): Map<LanguageOption, String> {
        return translations.associateBy({ it.language }, { it.content })
    }

    @Transactional
    fun assignUserRole(organizationId: Long, userId: Long, role: OrganizationRole) {
        if (userOrganizationRoleRepository.existsByOrganizationIdAndUserIdAndRole(organizationId, userId, role).not()) {
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

    @Transactional
    fun removeUserRoles(organizationId: Long, userId: Long) {
        val userOrganizationRoles = userOrganizationRoleRepository.findByOrganizationIdAndUserId(organizationId, userId)
        if (userOrganizationRoles.isNotEmpty()) {
            userOrganizationRoleRepository.deleteAll(userOrganizationRoles)
        }
    }
}
