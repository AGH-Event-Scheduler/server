package pl.edu.agh.server.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.domain.authentication.AuthenticationService
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import pl.edu.agh.server.domain.translation.LanguageOption
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

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

            createOrganizationsAndEvents()
            createUsers()
        }
    }

    private fun removeImages() {
        imageStorage.getAllSavedImageIds().forEach { imageStorage.deleteImage(it) }
    }

    private fun createOrganizationsAndEvents() {
        val knBit = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN BIT", LanguageOption.EN to "KN BIT"),
            logoImageFile = getFile("logo-kn-bit.jpg"),
            backgroundImageFile = getFile("bg-kn-bit.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło naukowe BIT to przede wszystkim miejsce, gdzie studenci AGH mogą rozwijać swoje zainteresowania i się nimi dzielić. Znajdziemy tutaj ludzi zainteresowanych nowoczesnymi metodami tworzenia oprogramowania, aktualnymi technologiami webowymi, a także algorytmami czy sztuczną inteligencją.\n" +
                    "\n" +
                    "Oprócz rozwijania swoich pasji, dzielimy się też wiedzą z zakresu studiów, prowadząc zajęcia wprowadzające w dziedziny matematyki i informatyki dla osób, które wcześniej nie miały z danymi zagadnieniami styczności. Wszystko po to, żeby wymienić się doświadczeniem i szybciej oraz lepiej przygotować się na zajęcia na uczelni.",
                LanguageOption.EN to "The BIT scientific club is, above all, a place where AGH students can develop their interests and share them. Here we will find people interested in modern software development methods, current web technologies, as well as algorithms and artificial intelligence.\n" +
                    "\n" +
                    "In addition to developing our passions, we also share our knowledge in the field of studies by conducting introductory classes in the fields of mathematics and computer science for people who have not had contact with these issues before. All this to exchange experience and prepare faster and better for classes at the university.",
            ),
        )

        val knSociology = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Osób Studiujących Socjologię", LanguageOption.EN to "SC of People Studying Sociology"),
            logoImageFile = getFile("logo-kn-sociology.png"),
            backgroundImageFile = getFile("bg-kn-sociology.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło Naukowe Studentów Socjologii zrzesza pasjonatów nauk społecznych oraz humanistycznych.\n" +
                    "\n" +
                    "Wspólnymi siłami działamy na rzecz popularyzacji nauki, a w szczególności socjologii. Poprzez realizowane projekty poznajemy metody i techniki badań – użyteczne nie tylko w świecie akademickim, ale i na rynku pracy.\n",
                LanguageOption.EN to "The Sociology Students' Scientific Club brings together enthusiasts of social sciences and humanities.\n" +
                    "\n" +
                    "Together, we work to popularize science, especially sociology. Through implemented projects, we learn research methods and techniques - useful not only in the academic world, but also on the labor market.",
            ),
        )

        val biomedical = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "BioMedical Innovations", LanguageOption.EN to "BioMedical Innovations"),
            logoImageFile = getFile("logo-biomedical.jpeg"),
            backgroundImageFile = getFile("bg-biomedical.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Celem Koło Naukowe BioMedical Innovations jest rozbudzanie zainteresowań pracą naukową oraz badawczą w zakresie przetwarzania i analizy obrazów cyfrowych, stosowanych we współczesnej biologii i medycynie. Prezentujemy swoje osiągnięcia i dzielą się doświadczeniami, przemyśleniami, wnioskami oraz analizą z resztą członków, tworząc atmosferę sprzyjającą rozwojowi w jakże interesującej dziedzinie obrazowania medycznego.",
                LanguageOption.EN to "The aim of the BioMedical Innovations Scientific Club is to stimulate interest in scientific and research work in the field of processing and analysis of digital images used in modern biology and medicine. We present our achievements and share experiences, thoughts, conclusions and analysis with the rest of the members, creating an atmosphere conducive to development in the interesting field of medical imaging.",
            ),
        )

        val creative = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "Koło Naukowe Creative", LanguageOption.EN to "Creative Scientific Club"),
            logoImageFile = getFile("logo-creative.png"),
            backgroundImageFile = getFile("bg-creative.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "WIELE OBSZARÓW\n" +
                    "\n" +
                    "Kreatywne zrzesza studentów wszystkich wydziałów Akademii Górniczo-Hutniczej w Krakowie. Głównym celem naszej działalności jest umożliwienie ambitnym studentom rozwijania swoich zainteresowań. Zajmujemy się wieloma gałęziami przemysłu, w których wykorzystuje się informatykę, np. w inżynierii materiałowej czy metalurgii.\n" +
                    "\nWYDARZENIA\n" +
                    "\n" +
                    "Nasze projekty mają szansę zostać wyróżnione na sesjach kół naukowych, w których nasi członkowie zdobywają najwyższe miejsca w swoich kategoriach. Organizujemy warsztaty i szkolenia prowadzone zarówno przez specjalistów w danej dziedzinie, jak i przez nas samych.\n" +
                    "\nSWOBODA\n" +
                    "\n" +
                    "Członkowie i sympatycy naszego koła to osoby zafascynowane robotyką, modelowaniem procesów fizycznych (w tym MES), a także osoby zajmujące się programowaniem aplikacji mobilnych i webowych czy zastosowaniem sztucznej inteligencji. Mocno wierzymy w ideę wspólnego uczenia się poprzez spotkania, warsztaty, wykłady i realizowane projekty.\n" +
                    "\nSZKOLENIA I OFERTY PRACY\n" +
                    "\n" +
                    "Współpracujemy z firmami i zespołami badawczymi na terenie miasta Krakowa i nie tylko. W wyniku współpracy członkowie Creative mają możliwość uczestniczenia w wyjazdach do ośrodków badawczych, biur inżynieryjnych i zakładów produkcyjnych, gdzie mogą dowiedzieć się więcej o profilu firmy. Co więcej, często wymieniane firmy przesyłają nam informacje o możliwości odbycia stażu lub pracy w ich firmie, ponieważ cenią sobie aktywność naszego koła.",
                LanguageOption.EN to "MANY AREAS\n" +
                    "\n" +
                    "Creative gathers students from all faculties of the AGH University of Science and Technology in Krakow. The main goal of our activity is to enable ambitious students to develop their interests. We deal with many branches, where IT is used, for example in materials science or metallurgy.\n" +
                    "\nEVENTS\n" +
                    "\n" +
                    "Our projects have a chance to win awards in a session of scientific clubs in which our members gain the highest places in their categories. We organize workshops and training conducted by specialists in a given field as well as by ourselves.\n" +
                    "\nFREEDOM\n" +
                    "\n" +
                    "The members and sympathizers of our circle are people fascinated by robotics, physical process modeling (including FEM) as well as people involved in the programming of mobile and web applications or the application of artificial intelligence. We strongly believe in the idea of common learning through meetings, workshops, lectures and ongoing projects.\n" +
                    "\nTRAINING AND JOB OFFERS\n" +
                    "\n" +
                    "We work with companies and research teams in the city of Krakow and beyond. As a result of cooperation, Creative members have the opportunity to participate in trips to research centers, engineering offices and production plants, where they can learn more about the company’s profile. What’s more, often mentioned companies send us information about the possibility of taking up an internship or work in their company because they value the activity of our circle.",
            ),
        )

        val ecoEnergy = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "AGH Eko-Energia", LanguageOption.EN to "AGH Eco-Energy"),
            logoImageFile = getFile("logo-eco-energy.jpg"),
            backgroundImageFile = getFile("bg-eco-energy.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "AGH Eko-Energia jest jednym z największych kół naukowych działających na Akademii Górniczo-Hutniczej w Krakowie. Rozwijane przez nas projektu koncentrują się na branży odnawialnych źródeł energii i nowoczesnych technologii.",
                LanguageOption.EN to "AGH Eko-Energia is one of the largest scientific groups operating at the AGH University of Science and Technology in Krakow. The projects we develop focus on the renewable energy industry and modern technologies.",
            ),
        )

        val dataTeam = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "Koło Naukowe Data Team", LanguageOption.EN to "Data Team Scientific Club"),
            logoImageFile = getFile("logo-data-team.jpg"),
            backgroundImageFile = getFile("bg-data-team.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło Naukowe Geos Informatica",
                LanguageOption.EN to "Geos Informatica Scientific Club",
            ),
        )

        val forSociety = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN 4 Society", LanguageOption.EN to "SC 4 Society"),
            logoImageFile = getFile("logo-4-society.jpeg"),
            backgroundImageFile = getFile("bg-4-society.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło naukowe 4Society zajmuje się działalnością naukową z pogranicza nauk społecznych i technologii. Koło stawia sobie za cel poszerzanie wiedzy studentów, prowadzenie badań naukowych w kontekście zmieniającego się społeczeństwa informacyjnego, organizowanie spotkań i debat z ludźmi ze świata nauki oraz tworzenie wartościowych treści, zawierających rzetelną i przejrzystą wiedzę społeczną.",
                LanguageOption.EN to "The 4Society scientific club is engaged in scientific activities at the intersection of social sciences and technology. The group's goals are to expand students' knowledge, conduct scientific research in the context of the changing information society, organize meetings and debates with people from the world of science, and create valuable content containing reliable and transparent social knowledge.",
            ),
        )

        val energon = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Energon", LanguageOption.EN to "KN Energon"),
            logoImageFile = getFile("logo-energon.png"),
            backgroundImageFile = getFile("bg-energon.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "KN Energon jest kołem zrzeszającym studentów zafascynowanych inżynierią, technologią i biomimetyką. W naszej organizacji tworzymy projekty oparte o inżynierię mechanicnzą i materiałową. W projektach koncentrujemy się często na sposobie rozprowadzenia i reakcji energii w mechanizmach. W głównym projekcie, nad którym pracujemy, czyli trebuszu \"Huragan\" wykorzystujemy biomimetyzm, aby stworzyć nowoczesny trebusz o jak największej sprawności!",
                LanguageOption.EN to "KN Energon is a club of students fascinated by engineering, technology and biomimetics. In our organization, we create projects based on mechanical and material engineering. In projects, we often focus on the way energy is distributed and reacted in mechanisms. In the main project we are working on, the \"Hurricane\" trebuchet, we use biomimetism to create a modern trebuchet with the highest possible efficiency!",
            ),
        )

        val larp = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Larp AGH", LanguageOption.EN to "KN Larp AGH"),
            logoImageFile = getFile("logo-larp.png"),
            backgroundImageFile = getFile("bg-larp.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "KN Larp to organizacja studencka zrzeszająca miłośników LARPów (live-action role playing). LARP to wydarzenie z pogranicza gry i przedstawienia teatralnego, podczas którego uczestnicy odgrywają i przeżywają różnego rodzaju historie, osadzone w rozmaitych konwencjach fabularnych. Misją koła jest: organizacja i popularyzacja LARPów, badanie tego zjawiska społecznego oraz tworzenie metod i technologii wspomagających projektowanie LARPów i wzbogacających rozgrywkę. Do współpracy zapraszamy zarówno twórców, doświadczonych graczy, jak i osoby, które nie miały dotychczas styczności z tą formą aktywności. Zachęcamy też do udziału w przygotowanych przez nas wydarzeniach. Chcesz poznać nas lepiej, wziąć udział w naszych wydarzeniach lub dołączyć do zespołu? Skontaktuj się z nami! ",
                LanguageOption.EN to "KN Larp is a student organization bringing together LARP (live-action role playing) enthusiasts. LARP is an event on the border between a game and a theatrical performance, during which participants act out and experience various types of stories set in various plot conventions. The mission of the club is: to organize and popularize LARPs, research this social phenomenon and create methods and technologies supporting the design of LARPs and enriching the gameplay. We invite both creators, experienced players and people who have not had any contact with this form of activity to cooperate with us. We also encourage you to participate in the events we have prepared. Do you want to get to know us better, take part in our events or join the team? Contact us!",
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

    private fun getFile(name: String): MultipartFile {
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
