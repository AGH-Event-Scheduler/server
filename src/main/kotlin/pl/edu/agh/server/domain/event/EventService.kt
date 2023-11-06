package pl.edu.agh.server.domain.event

import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventFromOrganizationAndInDateRange
import pl.edu.agh.server.application.event.EventSpecification.Companion.eventInDateRange
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.dto.EventDTO
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.TranslationService
import pl.edu.agh.server.foundation.application.BaseServiceUtilities
import java.time.Instant
import java.util.*

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
    private val imageStorage: ImageStorage,
    private val imageService: ImageService,
    private val translationService: TranslationService,
    private val modelMapper: ModelMapper,
) : BaseServiceUtilities<Event>(eventRepository) {

    fun getAllFromOrganizationInDateRange(
        page: Int,
        size: Int,
        sort: String,
        organizationId: Long,
        type: EventsType,
        language: LanguageOption,
    ): List<EventDTO> {
        val organizationAndDateSpec = eventFromOrganizationAndInDateRange(
            organizationId,
            Date.from(Instant.now()),
            type,
        )

        val events = getAllWithSpecificationPageable(page, size, sort, organizationAndDateSpec)
        return getWithTranslations(events, language)
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
    ): EventDTO {
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

        return modelMapper.map(newEvent, EventDTO::class.java)
    }

    fun getEvent(id: Long, language: LanguageOption): Optional<EventDTO> {
        val event = eventRepository.findById(id)
        return getWithTranslations(event.get(), language)
    }

    fun getAllInDateRange(page: Int, size: Int, sort: String, startDate: Date, endDate: Date, language: LanguageOption): List<EventDTO> {
        val events = getAllWithSpecificationPageable(page, size, sort, eventInDateRange(startDate, endDate))
        return getWithTranslations(events, language)
    }

    private fun getWithTranslations(events: List<Event>, language: LanguageOption): List<EventDTO> {
        val translationIds = mutableListOf<UUID>()
        events.forEach {
            translationIds.add(it.name)
            translationIds.add(it.description)
            translationIds.add(it.location)
        }

        val translations = translationService.getTranslations(translationIds, language)
        val translationsMap = translations.associateBy({ it.translationId }, { it.content })

        val eventDTOs = events.map {
            modelMapper.map(it, EventDTO::class.java)
                .apply {
                    name = translationsMap[it.name] ?: ""
                    location = translationsMap[it.location] ?: ""
                    description = translationsMap[it.description] ?: ""
                }
        }

        return eventDTOs
    }

    private fun getWithTranslations(event: Event, language: LanguageOption): Optional<EventDTO> {
        return Optional.ofNullable(getWithTranslations(listOf(event), language).firstOrNull())
    }
}
