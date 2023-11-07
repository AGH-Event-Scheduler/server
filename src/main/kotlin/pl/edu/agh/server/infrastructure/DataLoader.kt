package pl.edu.agh.server.infrastructure

import org.apache.commons.lang3.math.NumberUtils.toLong
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.domain.authentication.AuthenticationService
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.TranslationRepository
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import kotlin.random.Random.Default.nextInt

@Configuration
class DataLoader(
    private val studentRepository: StudentRepository,
    private val organizationRepository: OrganizationRepository,
    private val authenticationService: AuthenticationService,
    private val eventRepository: EventRepository,
    private val imageStorage: ImageStorage,
    private val imageService: ImageService,
    private val translationRepository: TranslationRepository,
    private val eventService: EventService,
    @Value("\${file.ddl-auto}") private val fileDDLAuto: String,
    @Value("\${configuration.mock-data}") private val mockData: Boolean,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (fileDDLAuto == "create-drop") {
            removeImages()
        }
        if (mockData) {
            val student = Student("472924", "Kamil", "Błażewicz")
            studentRepository.save(student)

            createOrganizations()
            createEvents()
            createUsers()
        }
    }

    private fun removeImages() {
        imageStorage.getAllSavedImageIds().forEach { imageStorage.deleteImage(it) }
    }

    private fun createOrganizations() {
        organizationRepository.saveAll(
            listOf(
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-1.jpg"),
                    backgroundImage = createBackgroundImage("bg-1.jpg"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-2.png"),
                    backgroundImage = createBackgroundImage("bg-2.png"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-3.jpeg"),
                    backgroundImage = createBackgroundImage("bg-3.png"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-4.png"),
                    backgroundImage = createBackgroundImage("bg-4.png"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-5.jpg"),
                    backgroundImage = createBackgroundImage("bg-5.png"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-6.jpg"),
                    backgroundImage = createBackgroundImage("bg-6.png"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-7.jpeg"),
                    backgroundImage = createBackgroundImage("bg-7.jpg"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-8.png"),
                    backgroundImage = createBackgroundImage("bg-8.jpg"),
                    description = UUID.randomUUID(),
                ),
                Organization(
                    name = UUID.randomUUID(),
                    logoImage = createLogoImage("logo-9.png"),
                    backgroundImage = createBackgroundImage("bg-9.jpg"),
                    description = UUID.randomUUID(),
                ),
            ),
        )
    }

    private fun createUsers() {
        authenticationService.register(
            RegisterRequest(
                email = "admin@agh.edu.pl",
                password = "admin123",
                firstName = "admin",
                lastName = "admin",
            ),
        )
    }

    private fun createEvents() {
        val organizations = organizationRepository.findAll()
        val events = mutableListOf<Event>()
        // backgroundImage = null ----> backgroundImage = org.backgroundImage
        for (org: Organization in organizations) {
            val offset = nextInt(0, 30)
            events.addAll(listOf(
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 1", LanguageOption.EN to "Test event 1"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).minusMinutes(toLong(offset.toString()))),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).plusMinutes(45).minusMinutes(toLong(offset.toString()))),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 2", LanguageOption.EN to "Test event 2"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(1).plusMinutes(90).minusMinutes(toLong(offset.toString()))),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 3", LanguageOption.EN to "Test event 3"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(2).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 4", LanguageOption.EN to "Test event 4"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(1).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 5", LanguageOption.EN to "Test event 5"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(3).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().minusDays(3).plusMinutes(120).minusMinutes(toLong(offset.toString())),
                    ),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 6", LanguageOption.EN to "Test event 6"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                    backgroundImageSkip = true
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = null,
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 7", LanguageOption.EN to "Test event 7"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                    backgroundImageSkip = true
                ),
            ))

            org.events = events
            eventRepository.saveAll(events)
            organizationRepository.save(org)
        }
    }

    private fun createBackgroundImage(filename: String): BackgroundImage {
        val image: BufferedImage = ImageIO.read(File("./mock-images/$filename"))
        val imageId = imageStorage.generateImageId()
        val extension = imageStorage.getFileExtension(filename)
        imageStorage.createImageDirectory(imageId)
        imageStorage.saveFile(imageService.resize(image, BackgroundImage.BIG_SIZE[0], BackgroundImage.BIG_SIZE[1]), imageId, "big.$extension")
        imageStorage.saveFile(imageService.resize(image, BackgroundImage.MEDIUM_SIZE[0], BackgroundImage.MEDIUM_SIZE[1]), imageId, "medium.$extension")
        imageStorage.saveFile(imageService.resize(image, BackgroundImage.SMALL_SIZE[0], BackgroundImage.SMALL_SIZE[1]), imageId, "small.$extension")

        return BackgroundImage(imageId, "small.$extension", "medium.$extension", "big.$extension")
    }

    private fun createLogoImage(filename: String): LogoImage {
        val image: BufferedImage = ImageIO.read(File("./mock-images/$filename"))
        val imageId = imageStorage.generateImageId()
        val extension = imageStorage.getFileExtension(filename)
        imageStorage.createImageDirectory(imageId)
        imageStorage.saveFile(imageService.resize(image, LogoImage.BIG_SIZE[0], LogoImage.BIG_SIZE[1]), imageId, "big.$extension")
        imageStorage.saveFile(imageService.resize(image, LogoImage.MEDIUM_SIZE[0], LogoImage.MEDIUM_SIZE[1]), imageId, "medium.$extension")
        imageStorage.saveFile(imageService.resize(image, LogoImage.SMALL_SIZE[0], LogoImage.SMALL_SIZE[1]), imageId, "small.$extension")

        return LogoImage(imageId, "small.$extension", "medium.$extension", "big.$extension")
    }

    private fun longLoremIpsum(): String {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent tempor dapibus erat, non dignissim ipsum suscipit vitae. Vestibulum lacus tortor, ornare ut dignissim vitae, condimentum sagittis nisl. Sed scelerisque in ex quis elementum. Morbi molestie placerat congue. Proin ac purus nec augue laoreet feugiat ut ut enim.\n" +
            "\n" +
            "Mauris sed pulvinar justo, vel rutrum est. Donec sem justo, rhoncus quis posuere ut, finibus sed turpis. Aliquam pharetra venenatis sem vitae pretium. Cras semper eleifend tortor et imperdiet. Maecenas dictum consectetur nisi eu luctus. Suspendisse eu rutrum magna. Vivamus placerat blandit sem, a aliquet nisi lobortis sed.\n" +
            "\n" +
            "Aliquam vitae turpis dolor. In tristique dui velit, id blandit diam varius ac. Proin quam ligula, posuere a aliquam a, efficitur eget risus. Donec at fermentum arcu. Suspendisse bibendum ex sit amet odio commodo, vel maximus arcu suscipit."
    }

    private fun mediumLoremIpsum(): String {
        return "Mauris sed pulvinar justo, vel rutrum est. Donec sem justo, rhoncus quis posuere ut, finibus sed turpis. Aliquam pharetra venenatis sem vitae pretium. Cras semper eleifend tortor et imperdiet. Maecenas dictum consectetur nisi eu luctus. Suspendisse eu rutrum magna. Vivamus placerat blandit sem, a aliquet nisi lobortis sed.\n" +
            "\n" +
            "Aliquam vitae turpis dolor. In tristique dui velit, id blandit diam varius ac. Proin quam ligula, posuere a aliquam a, efficitur eget risus. Donec at fermentum arcu. Suspendisse bibendum ex sit amet odio commodo, vel maximus arcu suscipit."
    }

    private fun shortLoremIpsum(): String {
        return "Mauris sed pulvinar justo, vel rutrum est. Donec sem justo, rhoncus quis posuere ut, finibus sed turpis. Aliquam pharetra venenatis sem vitae pretium. Cras semper eleifend tortor et imperdiet. Maecenas dictum consectetur nisi eu luctus. Suspendisse eu rutrum magna. Vivamus placerat blandit sem, a aliquet nisi lobortis sed. "
    }

    private fun shortLoremIpsumEn(): String {
        return "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself."
    }

    private fun shortLoremIpsumPl(): String {
        return "Ale muszę wam wytłumaczyć, jak narodziła się ta błędna koncepcja denuncjacji przyjemności i chwalebnego bólu, a ja dam wam kompletną relację z systemu i objaśnię prawdziwe nauki wielkiego odkrywcy prawdy, mistrza-budowniczego ludzkiego szczęścia. Nikt nie odrzuca, nie lubi lub unika przyjemności samej w sobie."
    }
}
