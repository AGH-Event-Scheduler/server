package pl.edu.agh.server.domain.event

import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.exception.EventNotFoundException
import pl.edu.agh.server.domain.exception.UserNotFoundException
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.translation.LanguageOption
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
    fun createEvent(
        organizationId: Long,
        backgroundImage: MultipartFile,
        nameMap: Map<LanguageOption, String>,
        descriptionMap: Map<LanguageOption, String>,
        locationMap: Map<LanguageOption, String>,
        startDate: Date,
        endDate: Date,
    ): Event {
        val organization = organizationRepository.findById(organizationId).orElseThrow()

//        TODO make it transactional - remove created image on failure
        val savedBackgroundImage: BackgroundImage = imageService.createBackgroundImage(backgroundImage)

        val newEvent = Event(
            name = translationService.createTranslation(nameMap),
            description = translationService.createTranslation(descriptionMap),
            location = translationService.createTranslation(locationMap),
            startDate = startDate,
            endDate = endDate,
            organization = organization,
            backgroundImage = savedBackgroundImage,
        )

        organization.events.add(newEvent)
        eventRepository.save(newEvent)
        organizationRepository.save(organization)

        return newEvent
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

    fun transformToEventDTO(events: List<Event>, language: LanguageOption, userName: String? = null): List<EventDTO> {
        val organizations = mutableSetOf<Organization>()
        events.forEach { organizations.add(it.organization) }

        val organizationDTOs = organizationService.transformToOrganizationDTO(organizations.toList(), language, userName)
        val organizationMap = organizationDTOs.associateBy({ it.id }, { it })

        val translationIds = mutableSetOf<UUID>()
        events.forEach {
            translationIds.add(it.name)
            translationIds.add(it.description)
            translationIds.add(it.location)
        }

        val translations = translationService.getTranslations(translationIds.toList(), language)
        val translationsMap = translations.associateBy({ it.translationId }, { it.content })

        val user: Optional<User> = userName?.let { userRepository.findByEmail(it) } ?: Optional.empty()

        val eventDTOs = events.map {
            modelMapper.map(it, EventDTO::class.java)
                .apply {
                    nameTranslated = translationsMap[it.name] ?: ""
                    locationTranslated = translationsMap[it.location] ?: ""
                    descriptionTranslated = translationsMap[it.description] ?: ""
                    isSaved = (user.isPresent) && user.get().savedEvents.contains(it)
                    underOrganization = organizationMap[it.organization.id]
                }
        }

        return eventDTOs
    }

    fun transformToEventDTO(event: Event, language: LanguageOption, userName: String? = null): Optional<EventDTO> {
        return Optional.ofNullable(transformToEventDTO(listOf(event), language, userName).firstOrNull())
    }
}
