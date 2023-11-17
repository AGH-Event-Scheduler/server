package pl.edu.agh.server.infrastructure

import org.apache.commons.lang3.math.NumberUtils.toLong
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.mock.web.MockMultipartFile
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
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import pl.edu.agh.server.domain.translation.LanguageOption
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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
    private val eventService: EventService,
    private val organizationService: OrganizationService,
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
        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN BIT", LanguageOption.EN to "KN BIT"),
            logoImageFile = getFile("logo-1.jpg"),
            backgroundImageFile = getFile("bg-1.jpg"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Osób Studiujących Socjologię", LanguageOption.EN to "Osób Studiujących Socjologię"),
            logoImageFile = getFile("logo-2.png"),
            backgroundImageFile = getFile("bg-2.png"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "BioMedical Innovations", LanguageOption.EN to "BioMedical Innovations"),
            logoImageFile = getFile("logo-3.jpeg"),
            backgroundImageFile = getFile("bg-3.png"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "Koło Naukowe Creative", LanguageOption.EN to "Koło Naukowe Creative"),
            logoImageFile = getFile("logo-4.png"),
            backgroundImageFile = getFile("bg-4.png"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "AGH Eko-Energia", LanguageOption.EN to "AGH Eko-Energia"),
            logoImageFile = getFile("logo-5.jpg"),
            backgroundImageFile = getFile("bg-5.png"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "Koło Naukowe Data Team", LanguageOption.EN to "Naukowe Data Team"),
            logoImageFile = getFile("logo-6.jpg"),
            backgroundImageFile = getFile("bg-6.png"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN 4 Society", LanguageOption.EN to "KN 4 Society"),
            logoImageFile = getFile("logo-7.jpeg"),
            backgroundImageFile = getFile("bg-7.jpg"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )

        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Energon", LanguageOption.EN to "KN Energon"),
            logoImageFile = getFile("logo-8.png"),
            backgroundImageFile = getFile("bg-8.jpg"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
        )
        organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Larp AGH", LanguageOption.EN to "KN Larp AGH"),
            logoImageFile = getFile("logo-9.png"),
            backgroundImageFile = getFile("bg-9.jpg"),
            descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn())
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

        for (org: Organization in organizations) {
            val offset = nextInt(0, 30)
            events.addAll(listOf(
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 1", LanguageOption.EN to "Test event 1"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).minusMinutes(toLong(offset.toString()))),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).plusMinutes(45).minusMinutes(toLong(offset.toString()))),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 2", LanguageOption.EN to "Test event 2"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(1).plusMinutes(90).minusMinutes(toLong(offset.toString()))),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 3", LanguageOption.EN to "Test event 3"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(2).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 4", LanguageOption.EN to "Test event 4"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(1).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 5", LanguageOption.EN to "Test event 5"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(3).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().minusDays(3).plusMinutes(120).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 6", LanguageOption.EN to "Test event 6"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                ),
                eventService.createEvent(
                    organizationId = org.id!!,
                    backgroundImage = getFile("bg-1.jpg"),
                    nameMap = mapOf(LanguageOption.PL to "Testowe wydarzenie 7", LanguageOption.EN to "Test event 7"),
                    locationMap = mapOf(LanguageOption.PL to "AGH D17 4.27", LanguageOption.EN to "AGH D17 4.27"),
                    descriptionMap = mapOf(LanguageOption.PL to shortLoremIpsumPl(), LanguageOption.EN to shortLoremIpsumEn()),
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                ),
            ))

            org.events = events
            eventRepository.saveAll(events)
            organizationRepository.save(org)
        }
    }

    private fun getFile(name: String): MultipartFile{
        val imagePath = "./mock-images/"
        val extension = name.substring(name.lastIndexOf('.') + 1)
        val contentType = when (extension) {
            "jpg" -> "image/jpg"
            "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "text/plain"
        }
        val content = Files.readAllBytes(Paths.get(imagePath + name))
        return MockMultipartFile(name, name, contentType, content)
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
