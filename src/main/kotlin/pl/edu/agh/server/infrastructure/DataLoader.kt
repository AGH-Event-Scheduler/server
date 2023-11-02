package pl.edu.agh.server.infrastructure

import org.apache.commons.lang3.math.NumberUtils.toLong
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.domain.authentication.AuthenticationService
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.common.LogoImage
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.image.ImageResizeService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import java.awt.image.BufferedImage
import java.io.File
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.imageio.ImageIO
import kotlin.random.Random.Default.nextInt

@Configuration
class DataLoader(
    private val studentRepository: StudentRepository,
    private val organizationRepository: OrganizationRepository,
    private val authenticationService: AuthenticationService,
    private val eventRepository: EventRepository,
    private val imageStorage: ImageStorage,
    private val imageResizeService: ImageResizeService,
    @Value("\${file.ddl-auto}") private val fileDDLAuto: String,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val student = Student("472924", "Kamil", "Błażewicz")
        studentRepository.save(student)

        if (fileDDLAuto == "create-drop") {
            removeImages()
        }
        createOrganizations()
        createEvents()
        createUsers()
    }

    private fun removeImages() {
        imageStorage.getAllSavedImageIds().forEach { imageStorage.deleteImage(it) }
    }

    private fun createOrganizations() {
        organizationRepository.saveAll(
            listOf(
                Organization(
                    name = "KN BIT",
                    logoImage = createLogoImage("logo-1.jpg"),
                    backgroundImage = createBackgroundImage("bg-1.jpg"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "KN Osób Studiujących Socjologię",
                    logoImage = createLogoImage("logo-2.png"),
                    backgroundImage = createBackgroundImage("bg-2.png"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "BioMedical Innovations",
                    logoImage = createLogoImage("logo-3.jpeg"),
                    backgroundImage = createBackgroundImage("bg-3.png"),
                    description = shortLoremIpsum(),
                ),
                Organization(
                    name = "Koło Naukowe Creative",
                    logoImage = createLogoImage("logo-4.png"),
                    backgroundImage = createBackgroundImage("bg-4.png"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "AGH Eko-Energia",
                    logoImage = createLogoImage("logo-5.jpg"),
                    backgroundImage = createBackgroundImage("bg-5.png"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "Koło Naukowe Data Team",
                    logoImage = createLogoImage("logo-6.jpg"),
                    backgroundImage = createBackgroundImage("bg-6.png"),
                    description = shortLoremIpsum(),
                ),
                Organization(
                    name = "KN 4 Society",
                    logoImage = createLogoImage("logo-7.jpeg"),
                    backgroundImage = createBackgroundImage("bg-7.jpg"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "KN Energon",
                    logoImage = createLogoImage("logo-8.png"),
                    backgroundImage = createBackgroundImage("bg-8.jpg"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "KN Larp AGH",
                    logoImage = createLogoImage("logo-9.png"),
                    backgroundImage = createBackgroundImage("bg-9.jpg"),
                    description = shortLoremIpsum(),
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
        for (org: Organization in organizations) {
            var offset = nextInt(0, 30)
            var events = listOf(
                Event(
                    name = "Test Event 0",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = shortLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).plusMinutes(45).minusMinutes(toLong(offset.toString()))),
                ),
                Event(
                    name = "Test Event 1",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = shortLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(1).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                Event(
                    name = "Test Event 2",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = longLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().plusDays(2).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                Event(
                    name = "Test Event 3",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = mediumLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(2).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().minusDays(2).plusMinutes(90).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                Event(
                    name = "Test Event 4",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = longLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(3).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(
                        LocalDateTime.now().minusDays(3).plusMinutes(120).minusMinutes(toLong(offset.toString())),
                    ),
                ),
                Event(
                    name = "Test Event 5",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = shortLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(4).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                ),
                Event(
                    name = "Test Event 6",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = shortLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).minusMinutes(toLong(offset.toString()))),
                    endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).plusMinutes(60).minusMinutes(toLong(offset.toString()))),
                ),
            )

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
        imageStorage.saveFile(imageResizeService.resize(image, BackgroundImage.BIG_SIZE[0], BackgroundImage.BIG_SIZE[1]), imageId, "big.$extension")
        imageStorage.saveFile(imageResizeService.resize(image, BackgroundImage.MEDIUM_SIZE[0], BackgroundImage.MEDIUM_SIZE[1]), imageId, "medium.$extension")
        imageStorage.saveFile(imageResizeService.resize(image, BackgroundImage.SMALL_SIZE[0], BackgroundImage.SMALL_SIZE[1]), imageId, "small.$extension")

        return BackgroundImage(imageId, "small.$extension", "medium.$extension", "big.$extension")
    }

    private fun createLogoImage(filename: String): LogoImage {
        val image: BufferedImage = ImageIO.read(File("./mock-images/$filename"))
        val imageId = imageStorage.generateImageId()
        val extension = imageStorage.getFileExtension(filename)
        imageStorage.createImageDirectory(imageId)
        imageStorage.saveFile(imageResizeService.resize(image, LogoImage.BIG_SIZE[0], LogoImage.BIG_SIZE[1]), imageId, "big.$extension")
        imageStorage.saveFile(imageResizeService.resize(image, LogoImage.MEDIUM_SIZE[0], LogoImage.MEDIUM_SIZE[1]), imageId, "medium.$extension")
        imageStorage.saveFile(imageResizeService.resize(image, LogoImage.SMALL_SIZE[0], LogoImage.SMALL_SIZE[1]), imageId, "small.$extension")

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
}
