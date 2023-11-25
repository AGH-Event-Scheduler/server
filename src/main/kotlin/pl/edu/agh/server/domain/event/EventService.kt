package pl.edu.agh.server.domain.event

import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.dto.FullEventDTO
import pl.edu.agh.server.domain.dto.OrganizationDTO
import pl.edu.agh.server.domain.exception.EventNotFoundException
import pl.edu.agh.server.domain.exception.OrganizationNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageService.IncorrectFileUploadException
import pl.edu.agh.server.domain.notification.NotificationService
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.translation.TranslationService
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.foundation.application.BaseServiceUtilities
import java.util.*

@Service
class EventService(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val modelMapper: ModelMapper,
    private val translationService: TranslationService,
    private val organizationRepository: OrganizationRepository,
    private val imageService: ImageService,
    private val organizationService: OrganizationService,
    private val notificationService: NotificationService,
) : BaseServiceUtilities<Event>(eventRepository) {

    @Transactional
    fun saveEventForUser(userName: String, eventId: Long) {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val event = eventRepository.findById(eventId)
            .orElseThrow { throw EventNotFoundException(eventId) }

        user.savedEvents.add(event)
        userRepository.save(user)
    }

    @Transactional
    fun removeEventForUser(userName: String, organizationId: Long) {
        val user = userRepository.findByEmail(userName).orElseThrow { throw UserNotFoundException(userName) }
        val event = eventRepository.findById(organizationId)
            .orElseThrow { throw EventNotFoundException(organizationId) }

        user.savedEvents.remove(event)
        userRepository.save(user)
    }

    @Transactional
    fun cancelEvent(eventId: Long) {
        val event = eventRepository.findById(eventId)
            .orElseThrow { throw EventNotFoundException(eventId) }
        event.canceled = true
        val updatedEvent = eventRepository.save(event)
        notificationService.notifyAboutEventCancel(updatedEvent)
    }

    @Transactional
    fun reactivateEvent(eventId: Long) {
        val event = eventRepository.findById(eventId)
            .orElseThrow { throw EventNotFoundException(eventId) }
        event.canceled = false
        val updatedEvent = eventRepository.save(event)
        notificationService.notifyAboutEventReactivate(updatedEvent)
    }

    @Transactional
    fun createEvent(
        organizationId: Long,
        backgroundImage: MultipartFile?,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
        locationMap: Map<LanguageOption, String>,
        startDate: Date,
        endDate: Date,
    ): Event {
        val savedBackgroundImage: BackgroundImage
        val organization = organizationRepository.findById(organizationId).orElseThrow { OrganizationNotFoundException(organizationId) }

//        TODO make it transactional - remove created image on failure
        if (backgroundImage != null) {
            savedBackgroundImage = imageService.createBackgroundImage(backgroundImage)
        } else {
            throw IncorrectFileUploadException("Uploaded file does not exist")
        }

        val newEvent = Event(
            name = translationService.createTranslation(nameMap),
            description = translationService.createTranslation(descriptionMap),
            location = translationService.createTranslation(locationMap),
            startDate = startDate,
            endDate = endDate,
            organization = organization,
            backgroundImage = savedBackgroundImage,
        )

        val savedEvent = eventRepository.save(newEvent)
        organization.events.add(savedEvent)
        organizationRepository.save(organization)

        notificationService.notifyAboutEventCreation(savedEvent)

        return savedEvent
    }

    @Transactional
    fun updateEvent(
        eventId: Long,
        backgroundImage: MultipartFile?,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
        locationMap: Map<LanguageOption, String>,
        startDate: Date,
        endDate: Date,
    ): Event {
        val event = eventRepository.findById(eventId).orElseThrow { throw EventNotFoundException(eventId) }

//        TODO make it transactional - remove previous image and current on failure
        if (backgroundImage != null) {
            val savedBackgroundImage: BackgroundImage = imageService.createBackgroundImage(backgroundImage)
            event.backgroundImage = savedBackgroundImage
        }

        event.name = translationService.createTranslation(nameMap)
        event.description = translationService.createTranslation(descriptionMap)
        event.location = translationService.createTranslation(locationMap)
        event.startDate = startDate
        event.endDate = endDate

        val savedEvent = eventRepository.save(event)

        notificationService.notifyAboutEventUpdate(savedEvent)

        return savedEvent
    }

    fun getEvent(eventId: Long): Event {
        return eventRepository.findById(eventId)
            .orElseThrow { throw EventNotFoundException(eventId) }
    }

//    FIXME use function from base service once NullPointerException is fixed
    override fun getAllWithSpecificationPageable(
        specification: Specification<Event>,
        pageable: PageRequest,
    ): List<Event> {
        return eventRepository.findAll(specification, pageable).content
    }

    fun transformToFullEventDTO(events: List<Event>): List<FullEventDTO> {
        val fullEventDTOs = events.map {
            modelMapper.map(it, FullEventDTO::class.java)
                .apply {
                    nameMap = getTranslationMap(it.name)
                    descriptionMap = getTranslationMap(it.description)
                    locationMap = getTranslationMap(it.location)
                }
        }

        return fullEventDTOs
    }

    fun transformToFullEventDTO(event: Event): FullEventDTO {
        return transformToFullEventDTO(listOf(event)).first()
    }

    fun transformToEventDTO(events: List<Event>, language: LanguageOption, userName: String? = null): List<EventDTO> {
        val organizationMap = getOrganizationDTOMap(events, language, userName)

        val user: Optional<User> = userName?.let { userRepository.findByEmail(it) } ?: Optional.empty()

        val eventDTOs = events.map {
            modelMapper.map(it, EventDTO::class.java)
                .apply {
                    nameTranslated = getTranslatedContent(it.name, language)
                    locationTranslated = getTranslatedContent(it.location, language)
                    descriptionTranslated = getTranslatedContent(it.description, language)
                    isSaved = (user.isPresent) && user.get().savedEvents.contains(it)
                    underOrganization = organizationMap[it.organization.id]
                }
        }

        return eventDTOs
    }

    fun transformToEventDTO(event: Event, language: LanguageOption, userName: String? = null): EventDTO {
        return transformToEventDTO(listOf(event), language, userName).first()
    }

    private fun getTranslatedContent(translations: Set<Translation>, language: LanguageOption): String {
        return translations.firstOrNull { translation -> translation.language === language }?.content ?: ""
    }

    private fun getTranslationMap(translations: Set<Translation>): Map<LanguageOption, String> {
        return translations.associateBy({ it.language }, { it.content })
    }

    private fun getOrganizationDTOMap(events: List<Event>, language: LanguageOption, userName: String?): Map<Long, OrganizationDTO> {
        val organizations = mutableSetOf<Organization>()
        events.forEach { organizations.add(it.organization) }

        val organizationDTOs = organizationService.transformToOrganizationDTO(organizations.toList(), language, userName)
        return organizationDTOs.associateBy({ it.id!! }, { it })
    }
}
