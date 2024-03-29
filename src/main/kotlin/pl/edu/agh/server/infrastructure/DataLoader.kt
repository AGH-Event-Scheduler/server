package pl.edu.agh.server.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.domain.authentication.AuthenticationService
import pl.edu.agh.server.domain.event.EventService
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.ImageService
import pl.edu.agh.server.domain.image.ImageStorage
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.organization.OrganizationService
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.user.Role
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.domain.user.organizationroles.OrganizationRole
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.imageio.ImageIO

@Configuration
class DataLoader(
    private val authenticationService: AuthenticationService,
    private val imageStorage: ImageStorage,
    private val imageService: ImageService,
    private val eventService: EventService,
    private val organizationService: OrganizationService,
    private val userRepository: UserRepository,
    @Value("\${file.ddl-auto}") private val fileDDLAuto: String,
    @Value("\${configuration.mock-data}") private val mockData: Boolean,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (fileDDLAuto == "create-drop") {
            removeImages()
        }
        if (mockData) {
            createUsers()
            createOrganizationsAndEvents()
            assignRoles()
        }
    }

    private fun removeImages() {
        imageStorage.getAllSavedImageIds().forEach { imageStorage.deleteImage(it) }
    }

    private fun createOrganizationsAndEvents() {
        mockKnBit()
        mockKnSociety()
        mockBiomedical()
        mockCreative()
        mockEcoEnergy()
        mockDataTeam()
        mockForSociety()
        mockEnergon()
        mockLarp()
    }

    private fun mockKnBit() {
        val knBit = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN BIT", LanguageOption.EN to "KN BIT"),
            logoImageFile = getFile("logo-kn-bit.png"),
            backgroundImageFile = getFile("bg-kn-bit.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło naukowe BIT to przede wszystkim miejsce, gdzie studenci AGH mogą rozwijać swoje zainteresowania i się nimi dzielić. Znajdziemy tutaj ludzi zainteresowanych nowoczesnymi metodami tworzenia oprogramowania, aktualnymi technologiami webowymi, a także algorytmami czy sztuczną inteligencją.\n" +
                    "\n" +
                    "Oprócz rozwijania swoich pasji, dzielimy się też wiedzą z zakresu studiów, prowadząc zajęcia wprowadzające w dziedziny matematyki i informatyki dla osób, które wcześniej nie miały z danymi zagadnieniami styczności. Wszystko po to, żeby wymienić się doświadczeniem i szybciej oraz lepiej przygotować się na zajęcia na uczelni.",
                LanguageOption.EN to "The BIT scientific club is, above all, a place where AGH students can develop their interests and share them. Here we will find people interested in modern software development methods, current web technologies, as well as algorithms and artificial intelligence.\n" +
                    "\n" +
                    "In addition to developing our passions, we also share our knowledge in the field of studies by conducting introductory classes in the fields of mathematics and computer science for people who have not had contact with these issues before. All this to exchange experience and prepare faster and better for classes at the university.",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = knBit.id!!,
            backgroundImage = getFile("bg-kn-bit-react.jpg"),
            nameMap = mapOf(
                LanguageOption.PL to "Warsztaty React - Schibsted Dla Studentów",
                LanguageOption.EN to "React Workshop - Schibsted For Students",
            ),
            descriptionMap = mapOf(
                LanguageOption.PL to "Hejka\uD83D\uDC4B\n" +
                    "\nkolejne warsztaty od Schibsted już niedługo! Tym razem tematyką będzie REACT.\n" +
                    "\nSerdecznie zapraszamy do zapisów na warsztaty z React'a organizowane w ramach inicjatywy Schibsted for Students prowadzonej przez firmę - Schibsted Tech Polska \uD83D\uDE80\n" +
                    "\nCzas trwania samych warsztatów to około 3-4h. Przewidziana jest przerwa na luźny networking, lepsze poznanie się i zadanie dodatkowych pytań developerom \uD83E\uDD1D Podczas networkingu będziecie mogli zjeść smaczną pizze i się czegoś napić \uD83C\uDF55 \uD83C\uDF7B\n" +
                    "\n\uD83D\uDC68\u200D\uD83D\uDCBB Tematyka: React\n" +
                    "\nCzego potrzebujesz?\n" +
                    "-własnego sprzętu (laptop + ładowarka) \uD83D\uDC69\u200D\uD83D\uDCBB\uD83D\uDC68\u200D\uD83D\uDCBB\n" +
                    "-dobrego nastawienia i chęci nauki \uD83D\uDCAA\n" +
                    "\nLiczba miejsc OGRANICZONA. Obowiązują zapisy do 17.11.2023 godzina 23:59 - obowiązują zapisy! \uD83C\uDFAB\n" +
                    "\nZapraszamy i do zobaczenia wkrótce! \uD83D\uDE80\n" +
                    "\nLink do zapisów: https://forms.office.com/e/5UHNq3sLjF\n" +
                    "\n(Po zamknięciu zapisów, do 20.11.2023 dostaniecie szczegółowe informacje na podanego maila)",
                LanguageOption.EN to "Hey\uD83D\uDC4B\n" +
                    "\nnext workshops from Schibsted coming soon! This time the topic will be REACT.\n" +
                    "\nWe cordially invite you to sign up for React workshops organized as part of the Schibsted for Students initiative run by the company - Schibsted Tech Polska \uD83D\uDE80\n" +
                    "\nThe duration of the workshops itself is approximately 3-4 hours. There will be a break for casual networking, getting to know each other better and asking additional questions to developers \uD83E\uDD1D During the networking you will be able to eat tasty pizza and drink something \uD83C\uDF55 \uD83C\uDF7B\n" +
                    "\n\uD83D\uDC68\u200D\uD83D\uDCBB Subject: React\n" +
                    "\nWhat you need?\n" +
                    "-your own equipment (laptop + charger) \uD83D\uDC69\u200D\uD83D\uDCBB\uD83D\uDC68\u200D\uD83D\uDCBB\n" +
                    "-good attitude and willingness to learn \uD83D\uDCAA\n" +
                    "\nThe number of places is LIMITED. Registration required until November 17, 2023 at 11:59 p.m. - registration required! \uD83C\uDFAB\n" +
                    "\nWelcome and see you soon! \uD83D\uDE80\n" +
                    "\nLink to registration: https://forms.office.com/e/5UHNq3sLjF\n" +
                    "\n(After registration closes, you will receive detailed information by November 20, 2023 by e-mail)",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Biuro Schibsted Tech Polska - ul. Pawia 23, 31-154 Kraków",
                LanguageOption.EN to "Kraków office - Schibsted Tech Polska - St. Pawia 23, 31-154 Kraków",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5)),
            endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5).plusHours(4)),
        )
        eventService.createEvent(
            organizationId = knBit.id!!,
            backgroundImage = getFile("bg-kn-bit-algo.jpg"),
            nameMap = mapOf(
                LanguageOption.PL to "Akademickie Mistrzostwa Polski w Programowaniu Zespołowym",
                LanguageOption.EN to "Polish Academic Championship in Team Programming",
            ),
            descriptionMap = mapOf(
                LanguageOption.PL to "Hej!\n" +
                    "\n" +
                    "Jeżeli interesują was algorytmy i/lub programowanie zespołowe, to właśnie dla was mamy coś specjalnego!\n" +
                    "\n" +
                    "Jeżeli chcecie sprawdzić swoje umiejętności i zobaczyć, czy jesteście w stanie rozwiązać zadania na poziomie AMPPZ, to jest to idealna okazja!\n" +
                    "\n" +
                    "Drużyny jadące na CERC jadą na AMPy automatycznie, tymi eliminacjami chcemy wyłonić jeszcze dwie dodatkowe drużyny reprezentujące AGH.\n" +
                    "\n" +
                    "Same AMPPZy są w tym roku w Warszawie, 3-5 listopada.\n" +
                    "\n" +
                    "Serdecznie zapraszam wszystkich zainteresowanych ze swoimi laptopami na eliminacje \uD83D\uDE09\n" +
                    "\n" +
                    "PS: Zapraszam także oczywiście osoby z CERCa w ramach treningu.\n" +
                    "\n" +
                    "PSS: wiecej szczegolow na Discordzie BITu -> https://discord.gg/JEhGe9psu6",
                LanguageOption.EN to "Hi!\n" +
                    "\n" +
                    "If you were curious about algorithms and/or team programming, this was something special for us!\n" +
                    "\n" +
                    "Once you check if you can find them, if you are capable of AMPPZ level tasks, this is perfect!\n" +
                    "\n" +
                    "Teams going to CERC go to AMPs automatically; these qualifying rounds will reveal two additional characteristics of AGH.\n" +
                    "\n" +
                    "The AMPPZs themselves are this year in Warsaw, November 3-5.\n" +
                    "\n" +
                    "We cordially encourage everyone interested with their laptops to eliminate it \uD83D\uDE09\n" +
                    "\n" +
                    "PS: Of course, also access to people from CERC as part of the training.\n" +
                    "\n" +
                    "PSS: more details on BIT's Discord -> https://discord.gg/JEhGe9psu6",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "AGH D17 3.27c",
                LanguageOption.EN to "AGH D17 3.27c",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(5)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(5).plusHours(2).plusMinutes(30)),
        )
    }

    private fun mockKnSociety() {
        val knSociology = organizationService.createOrganization(
            nameMap = mapOf(
                LanguageOption.PL to "KN Osób Studiujących Socjologię",
                LanguageOption.EN to "SC of People Studying Sociology",
            ),
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
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = knSociology.id!!,
            backgroundImage = getFile("bg-kn-sociology-rekru.jpg"),
            nameMap = mapOf(LanguageOption.PL to "Rekrutacja", LanguageOption.EN to "Recruitment"),
            descriptionMap = mapOf(
                LanguageOption.PL to "❗\uFE0FRuszamy z rekrutacją❗\uFE0F\n" +
                    "\n" +
                    "Czy zastanawialiście się nad dołączeniem do KNOSS AGH? \uD83E\uDD14 \n" +
                    "\n" +
                    "Jeśli tak, to nie zastanawiajcie się dłużej tylko spróbujcie swoich sił w naszym zespole! \uD83E\uDD29\n" +
                    "\n" +
                    "Już niebawem ruszamy z rekrutacją, w której każdy znajdzie coś dla siebie. \n" +
                    "\n" +
                    "Nagrywanie TikToków? \uD83C\uDFA5\n" +
                    "\n" +
                    "Prowadzenie wywiadów w ramach podcastu?\uD83C\uDF99\uFE0F\n" +
                    "\n" +
                    "Udział w projektach badawczych? \uD83D\uDC69\uD83C\uDFFC\u200D\uD83D\uDD2C\n" +
                    "\n" +
                    "Zdobywanie wiedzy socjologicznej? \uD83E\uDD1D\uD83C\uDFFC\n" +
                    "\n" +
                    "U nas znajdziesz to wszystko i wiele więcej! Poza rozwojem osobistym i zwiększaniem swoich kompetencji nawiążesz również wspaniałe znajomości! \uD83D\uDC65\n" +
                    "\n" +
                    "Co, jeśli nie studiujesz socjologii, ale chcesz dołączyć do KNOSSu? \n" +
                    "\n" +
                    "Zapraszamy! Z pewnością znajdziesz tu coś dla siebie.\n" +
                    "\n" +
                    "Rekrutacja odbędzie się już w drugiej połowie listopada, a zgłoszenia można dokonać poprzez formularz, który pojawi się na naszych profilach lub w siedzibie koła. \uD83D\uDC40\n" +
                    "\n" +
                    "Bądź na bieżąco i nie przegap dalszych szczegółów rekrutacji! \uD83E\uDD73",
                LanguageOption.EN to "❗\uFE0FWe are starting recruitment❗\uFE0F\n" +
                    "\n" +
                    "Have you considered joining KNOSS AGH? \uD83E\uDD14\n" +
                    "\n" +
                    "If so, don't hesitate any longer and try your hand at our team! \uD83E\uDD29\n" +
                    "\n" +
                    "We will soon start recruiting, where everyone will find something for themselves.\n" +
                    "\n" +
                    "Recording TikToks? \uD83C\uDFA5\n" +
                    "\n" +
                    "Conducting podcast interviews?\uD83C\uDF99\uFE0F\n" +
                    "\n" +
                    "Participation in research projects? \uD83D\uDC69\uD83C\uDFFC\u200D\uD83D\uDD2C\n" +
                    "\n" +
                    "Acquiring sociological knowledge? \uD83E\uDD1D\uD83C\uDFFC\n" +
                    "\n" +
                    "Here you will find all this and much more! In addition to personal development and increasing your competences, you will also make great friends! \uD83D\uDC65\n" +
                    "\n" +
                    "What if you don't study sociology but want to join KNOSS?\n" +
                    "\n" +
                    "We invite you! You will certainly find something for yourself here.\n" +
                    "\n" +
                    "Recruitment will take place in the second half of November, and applications can be submitted via the form that will appear on our profiles or in the Club office. \uD83D\uDC40\n" +
                    "\n" +
                    "Stay up to date and don't miss further recruitment details!   ",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "AGH D17 2.21",
                LanguageOption.EN to "AGH D17 2.21",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(9)),
        )
    }

    private fun mockBiomedical() {
        val biomedical = organizationService.createOrganization(
            nameMap = mapOf(
                LanguageOption.PL to "BioMedical Innovations",
                LanguageOption.EN to "BioMedical Innovations",
            ),
            logoImageFile = getFile("logo-biomedical.jpeg"),
            backgroundImageFile = getFile("bg-biomedical.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Celem Koło Naukowe BioMedical Innovations jest rozbudzanie zainteresowań pracą naukową oraz badawczą w zakresie przetwarzania i analizy obrazów cyfrowych, stosowanych we współczesnej biologii i medycynie. Prezentujemy swoje osiągnięcia i dzielą się doświadczeniami, przemyśleniami, wnioskami oraz analizą z resztą członków, tworząc atmosferę sprzyjającą rozwojowi w jakże interesującej dziedzinie obrazowania medycznego.",
                LanguageOption.EN to "The aim of the BioMedical Innovations Scientific Club is to stimulate interest in scientific and research work in the field of processing and analysis of digital images used in modern biology and medicine. We present our achievements and share experiences, thoughts, conclusions and analysis with the rest of the members, creating an atmosphere conducive to development in the interesting field of medical imaging.",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = biomedical.id!!,
            backgroundImage = getFile("bg-kn-biomedical-rekru.jpg"),
            nameMap = mapOf(LanguageOption.PL to "Spotkanie rekrutacyjne", LanguageOption.EN to "Recruitment meeting"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Czy interesuje Cię przetwarzanie i analiza obrazów?  \n" +
                    "\n" +
                    "Czy słowa \"medycyna\" i \"biologia\" wywołują u \n" +
                    "\n" +
                    "Ciebie przyspieszony puls?  A może masz za dużo czasu i chcesz wykorzystać go na pozytywny rozwój?\n" +
                    "\n" +
                    "Jeśli na którekolwiek pytanie Twoja odpowiedź brzmi twierdząco, to w takim razie zapraszamy na spotkanie rekrutacyjne naszego Koła Naukowego BioMedical Imaging! \n" +
                    "\n" +
                    "Koło Naukowe BioMedical Imaging zostało założone w listopadzie 2020 roku na Wydziale Elektrotechniki, Automatyki, Informatyki i Inżynierii Biomedycznej z inicjatywy studentów i pracowników AGH, których wspólnym celem jest rozbudzanie zainteresowań pracą naukową oraz badawczą w zakresie przetwarzania i analizy obrazów cyfrowych, stosowanych we współczesnej biologii i medycynie.\n" +
                    "\n" +
                    "Nie czekaj i zostań członkiem naszego Koła już teraz! \n" +
                    "\n" +
                    "Do zobaczenia!",
                LanguageOption.EN to "Are you interested in image processing and analysis?\n" +
                    "\n" +
                    "Do the words \"medicine\" and \"biology\" trigger u?\n" +
                    "\n" +
                    "Is your pulse racing? Or maybe you have too much time and want to use it for positive development?\n" +
                    "\n" +
                    "If your answer to any of these questions is yes, then we invite you to the recruitment meeting of our BioMedical Imaging Scientific Club!\n" +
                    "\n" +
                    "The BioMedical Imaging Scientific Club was founded in November 2020 at the Faculty of Electrical Engineering, Automation, Computer Science and Biomedical Engineering on the initiative of students and employees of AGH, whose common goal is to arouse interest in scientific and research work in the field of processing and analysis of digital images used in modern biology and medicine.\n" +
                    "\n" +
                    "Don't wait and become a member of our Club now!\n" +
                    "\n" +
                    "See you!",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "AGH B1 sala 121",
                LanguageOption.EN to "AGH B1 room 121",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).plusHours(3)),
        )
    }

    private fun mockCreative() {
        val creative = organizationService.createOrganization(
            nameMap = mapOf(
                LanguageOption.PL to "Koło Naukowe Creative",
                LanguageOption.EN to "Creative Scientific Club",
            ),
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
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = creative.id!!,
            backgroundImage = getFile("bg-kn-creative-science-night.jpg"),
            nameMap = mapOf(
                LanguageOption.PL to "Małopolska noc naukowców",
                LanguageOption.EN to "Recruitment meeting",
            ),
            descriptionMap = mapOf(
                LanguageOption.PL to "Zapraszamy od 18:00 na pokaz \"Z metalem można prawie wszystko\" \uD83E\uDDD0 \n" +
                    "\n" +
                    "Do zobaczenia ! \uD83D\uDC9A\uD83D\uDDA4❤\uFE0F\n" +
                    "\n" +
                    "https://lnkd.in/ddsbA8nW",
                LanguageOption.EN to "We invite you from 18:00 to the show \"You can almost do anything with metal\" \uD83E\uDDD0\n" +
                    "\n" +
                    "See you ! \uD83D\uDC9A\uD83D\uDDA4❤\uFE0F\n" +
                    "\n" +
                    "https://lnkd.in/ddsbA8nW",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Zdalnie",
                LanguageOption.EN to "Online",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).plusHours(4)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1).plusHours(7)),
        )
        eventService.createEvent(
            organizationId = creative.id!!,
            backgroundImage = getFile("bg-kn-creative-rekru.png"),
            nameMap = mapOf(LanguageOption.PL to "Rekrutujemy!", LanguageOption.EN to "We are recruiting!"),
            descriptionMap = mapOf(
                LanguageOption.PL to "\uD83D\uDD25Zaczynamy rekrutację!\uD83D\uDD25\n" +
                    "Szukasz sposobu na rozwinięcie swoich umiejętności? \n" +
                    "\n" +
                    "Jesteś otwarty na nowe doświadczenia?\n" +
                    "\n" +
                    "Chcesz na studiach robić coś wyjątkowego, albo masz pomysł na własny projekt?\n" +
                    "\n" +
                    "Jeśli tak, CREATIVE może być miejscem w ktorym powyzsze rzeczy zaczną się spełniać \uD83E\uDD16\n" +
                    "\n" +
                    "Kogo szukamy? \n" +
                    "\uD83D\uDC49 Elektroników i Robotyków,\n" +
                    "\uD83D\uDC49 Programistów,\n" +
                    "\uD83D\uDC49 Entuzjastów Sztucznej Inteligencji,\n" +
                    "\uD83D\uDC49 Mistrzów CADa, oraz Modelowania numerycznego (CFD/FEM/CA)\n" +
                    "\n" +
                    "Jeżeli jesteś, albo przynajmniej aspirujesz do bycia kimś z powyższej listy (ale nie tylko!) zachęcamy do uzupełnienia formularza poniżej:\n" +
                    "\n" +
                    "http://bit.ly/2Z3nAjH\n" +
                    "\n" +
                    "Pamiętajcie, że nie są to wszystkie z projektów Creative.\n" +
                    "\n" +
                    "Jesteśmy też otwarci na nowe pomysły \uD83C\uDF88\n" +
                    "\n" +
                    "Jednak przede wszystkim chcemy wam rozwijać swoje pasje i poznawac nowe ciekawe zagadnienia \uD83C\uDF1F\n" +
                    "\n" +
                    "Zapisy trwają do 6 Stycznia, do godziny 23.59 \uD83D\uDC49 ⏰\n" +
                    "\n" +
                    "Osoby ktore sie zapiszą poinformujemy o spotkaniu organizacyjnym, które odbędzie się:\n" +
                    "\uD83D\uDD25 9 Stycznia 2020 \uD83D\uDD25\n" +
                    "\n" +
                    "Nie czekajcie!",
                LanguageOption.EN to "\uD83D\uDD25We are starting recruitment!\uD83D\uDD25\n" +
                    "Are you looking for a way to develop your skills?\n" +
                    "\n" +
                    "Are you open to new experiences?\n" +
                    "\n" +
                    "Do you want to do something unique during your studies or do you have an idea for your own project?\n" +
                    "\n" +
                    "If so, CREATIVE may be the place where the above things will come true \uD83E\uDD16\n" +
                    "\n" +
                    "Who are we looking for?\n" +
                    "\uD83D\uDC49 Electronics and Robotics,\n" +
                    "\uD83D\uDC49 Programmers,\n" +
                    "\uD83D\uDC49 Artificial Intelligence enthusiasts,\n" +
                    "\uD83D\uDC49 Masters of CAD and Numerical Modeling (CFD/FEM/CA)\n" +
                    "\n" +
                    "If you are, or at least aspire to be, someone from the above list (but not only!), we encourage you to complete the form below:\n" +
                    "\n" +
                    "http://bit.ly/2Z3nAjH\n" +
                    "\n" +
                    "Please note that these are not all Creative designs.\n" +
                    "\n" +
                    "We are also open to new ideas \uD83C\uDF88\n" +
                    "\n" +
                    "However, above all, we want you to develop your passions and learn new interesting issues \uD83C\uDF1F\n" +
                    "\n" +
                    "Registration lasts until January 6, 11.59 p.m. \uD83D\uDC49 ⏰\n" +
                    "\n" +
                    "We will inform those who register about the organizational meeting that will take place:\n" +
                    "\uD83D\uDD25 January 9, 2020 \uD83D\uDD25\n" +
                    "\n" +
                    "Don't wait!",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Zdalnie",
                LanguageOption.EN to "Online",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(3).plusHours(4).plusMinutes(30)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(6).plusHours(4).plusMinutes(30)),
        )
    }

    private fun mockEcoEnergy() {
        val ecoEnergy = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "AGH Eko-Energia", LanguageOption.EN to "AGH Eco-Energy"),
            logoImageFile = getFile("logo-eco-energy.jpg"),
            backgroundImageFile = getFile("bg-eco-energy.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "AGH Eko-Energia jest jednym z największych kół naukowych działających na Akademii Górniczo-Hutniczej w Krakowie. Rozwijane przez nas projektu koncentrują się na branży odnawialnych źródeł energii i nowoczesnych technologii.",
                LanguageOption.EN to "AGH Eko-Energia is one of the largest scientific groups operating at the AGH University of Science and Technology in Krakow. The projects we develop focus on the renewable energy industry and modern technologies.",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = ecoEnergy.id!!,
            backgroundImage = getFile("bg-eco-energy-work.jpg"),
            nameMap = mapOf(
                LanguageOption.PL to "Targi Pracy Przedsiębiorczości",
                LanguageOption.EN to "Entrepreneurship Job Fair",
            ),
            descriptionMap = mapOf(
                LanguageOption.PL to "⏱Trwają zapisy na szkolenia oraz konsultacje biznesowe w ramach Targów Pracy Przedsiębiorczości “Majówka z Pracą”! \n" +
                    "\n" +
                    "Będziecie mogli tam spotkać i nas ;))\n" +
                    "\n" +
                    "Już 10 maja na Tauron Arenie Kraków Miastem Startupów przeprowadzą dla Was szkolenia z następujących tematyk:\n" +
                    "✅marketing,\n" +
                    "✅ pozyskiwanie funduszy dla organizacji studenckich oraz przedsiębiorstw,\n" +
                    "✅ wystąpienia publiczne,\n" +
                    "✅ zarządzanie projektami,\n" +
                    "✅zarządzanie zespołem,\n" +
                    "✅ innowacje społeczne,\n" +
                    "✅ wartości społeczne w strategiach biznesowych.\n" +
                    "\n" +
                    "Odbędą się również konsultacje biznesowe z ekspertem Pawłem Zdańkowskim, który pomoże rozwiązać wszelkie wątpliwości w prowadzeniu działalności biznesowych!\n" +
                    "\n" +
                    "Zapiszcie się korzystając z linku poniżej \n" +
                    "\n" +
                    "https://forms.gle/8TiYmDyTHG4dS13w6\n" +
                    "\n" +
                    "Do zobaczenia!",
                LanguageOption.EN to "⏱Registration for training and business consultations as part of the Entrepreneurship Job Fair \"Majówka z Pracy\" is underway!\n" +
                    "\n" +
                    "You will be able to meet us there too ;))\n" +
                    "\n" +
                    "On May 10, at the Tauron Arena Kraków City of Startups, they will conduct training for you on the following topics:\n" +
                    "✅marketing,\n" +
                    "✅ raising funds for student organizations and enterprises,\n" +
                    "✅ public speaking,\n" +
                    "✅ project management,\n" +
                    "✅team management,\n" +
                    "✅ social innovations,\n" +
                    "✅ social values \u200B\u200Bin business strategies.\n" +
                    "\n" +
                    "There will also be business consultations with expert Paweł Zdańkowski, who will help solve any doubts in running a business!\n" +
                    "\n" +
                    "Sign up using the link below\n" +
                    "\n" +
                    "https://forms.gle/8TiYmDyTHG4dS13w6\n" +
                    "\n" +
                    "See you!",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Tauron Arena Kraków",
                LanguageOption.EN to "Tauron Arena Kraków",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusHours(1).plusMinutes(30)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusHours(9).plusMinutes(30)),
        )
    }

    private fun mockDataTeam() {
        val dataTeam = organizationService.createOrganization(
            nameMap = mapOf(
                LanguageOption.PL to "Koło Naukowe Data Team",
                LanguageOption.EN to "Data Team Scientific Club",
            ),
            logoImageFile = getFile("logo-data-team.jpg"),
            backgroundImageFile = getFile("bg-data-team.png"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło Naukowe Geos Informatica",
                LanguageOption.EN to "Geos Informatica Scientific Club",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
    }

    private fun mockForSociety() {
        val forSociety = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN 4 Society", LanguageOption.EN to "SC 4 Society"),
            logoImageFile = getFile("logo-4-society.jpeg"),
            backgroundImageFile = getFile("bg-4-society.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Koło naukowe 4Society zajmuje się działalnością naukową z pogranicza nauk społecznych i technologii. Koło stawia sobie za cel poszerzanie wiedzy studentów, prowadzenie badań naukowych w kontekście zmieniającego się społeczeństwa informacyjnego, organizowanie spotkań i debat z ludźmi ze świata nauki oraz tworzenie wartościowych treści, zawierających rzetelną i przejrzystą wiedzę społeczną.",
                LanguageOption.EN to "The 4Society scientific club is engaged in scientific activities at the intersection of social sciences and technology. The group's goals are to expand students' knowledge, conduct scientific research in the context of the changing information society, organize meetings and debates with people from the world of science, and create valuable content containing reliable and transparent social knowledge.",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
    }

    private fun mockEnergon() {
        val energon = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Energon", LanguageOption.EN to "KN Energon"),
            logoImageFile = getFile("logo-energon.png"),
            backgroundImageFile = getFile("bg-energon.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "KN Energon jest kołem zrzeszającym studentów zafascynowanych inżynierią, technologią i biomimetyką. W naszej organizacji tworzymy projekty oparte o inżynierię mechanicnzą i materiałową. W projektach koncentrujemy się często na sposobie rozprowadzenia i reakcji energii w mechanizmach. W głównym projekcie, nad którym pracujemy, czyli trebuszu \"Huragan\" wykorzystujemy biomimetyzm, aby stworzyć nowoczesny trebusz o jak największej sprawności!",
                LanguageOption.EN to "KN Energon is a club of students fascinated by engineering, technology and biomimetics. In our organization, we create projects based on mechanical and material engineering. In projects, we often focus on the way energy is distributed and reacted in mechanisms. In the main project we are working on, the \"Hurricane\" trebuchet, we use biomimetism to create a modern trebuchet with the highest possible efficiency!",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = energon.id!!,
            backgroundImage = getFile("bg-energon-show.jpg"),
            nameMap = mapOf(
                LanguageOption.PL to "Pokaz Strzelania z Trebusza",
                LanguageOption.EN to "Trebuchet Shooting Demonstration",
            ),
            descriptionMap = mapOf(
                LanguageOption.PL to "TO JUŻ W TEN WEEKEND!\n" +
                    "\n" +
                    "Serdecznie zapraszamy Państwa na pokaz strzelania z trebusza #Huragan, który  wyrzuci 20 litrowy keg na rekordową odległość! \uD83D\uDE31\n" +
                    "\n" +
                    "Zapraszamy!",
                LanguageOption.EN to "IT'S THIS WEEKEND!\n" +
                    "\n" +
                    "We cordially invite you to the #Huragan trebuchet shooting show, which will throw a 20 liter keg to a record distance! \uD83D\uDE31\n" +
                    "\n" +
                    "We invite you!",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Miasto i Gmina Dubiecko - serwis samorządowy  w Powiecie Przemyskim",
                LanguageOption.EN to "City and Commune of Dubiecko - local government service in Przemyśl County",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusHours(5)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusHours(6).plusMinutes(30)),
        )
        eventService.createEvent(
            organizationId = energon.id!!,
            backgroundImage = getFile("bg-energon-open-days.jpg"),
            nameMap = mapOf(LanguageOption.PL to "Dzień Otwarty AGH", LanguageOption.EN to "AGH Open Day"),
            descriptionMap = mapOf(
                LanguageOption.PL to "DZIEJE SIĘ! na PLENEROWY Dzień Otwarty AGH 2023 \n" +
                    "\n" +
                    "Strasznie nam miło, że koło ENERGON \uD83D\uDC09oraz nasza ✨ Kaśka ✨ cieszy się tak dużym zainteresowaniem! \uD83E\uDD70\n" +
                    "\n" +
                    "A kto jeszcze Nas nie znalazł \n" +
                    "Zapraszamy pod  Centrum Dydaktyki AGH Pawilon U2 !\n" +
                    "\n" +
                    "Przy naszym stoisku:\n" +
                    "✅Trebusz ✨ Kaśka ✨gotowy do wystrzału \uD83D\uDE0F\n" +
                    "✅możecie sprawdzić swoja wiedzę na temat starożytnych maszyn oblężniczych \uD83D\uDE03\n" +
                    "✅dowiecie się więcej na tematy trebuszy i biomimetyki ☘\uFE0F\n" +
                    "✅Porozmawiajcie z Nami ❤\uFE0F a nawet umówmy się na MS AGH  na dyskusje \uD83D\uDE07\n" +
                    "✅upominek \uD83E\uDD2B\n" +
                    "\n" +
                    "Czekamy na Was o 15! \n" +
                    "\n" +
                    "Do zobaczenia \uD83D\uDE42\n" +
                    "✌",
                LanguageOption.EN to "HAPPENS! for the AGH OUTDOOR Open Day 2023\n" +
                    "\n" +
                    "We are very pleased that the ENERGON \uD83D\uDC09 group and our ✨ Kaśka ✨ enjoy so much interest!   \n" +
                    "\n" +
                    "And who hasn't found us yet?\n" +
                    "We invite you to the AGH Didactics Center, Pavilion U2!\n" +
                    "\n" +
                    "At our stand:\n" +
                    "✅Trebuchet ✨ Kaśka ✨ready to fire \uD83D\uDE0F\n" +
                    "✅ you can test your knowledge about ancient siege machines \uD83D\uDE03\n" +
                    "✅ you will learn more about trebuchets and biomimetics ☘\uFE0F\n" +
                    "✅Talk to us ❤\uFE0F and let's even make an appointment at MS AGH for discussions \uD83D\uDE07\n" +
                    "✅gift \uD83E\uDD2B\n" +
                    "\n" +
                    "We are waiting for you at 3 p.m.!\n" +
                    "\n" +
                    "See you \uD83D\uDE42\n" +
                    "✌",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "Centrum Dydaktyczne AGH, Pawilon U2",
                LanguageOption.EN to "AGH Didactics Center, Pavilion U2",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2).plusHours(5)),
            endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2).plusHours(6).plusMinutes(30)),
        )
    }

    private fun mockLarp() {
        val larp = organizationService.createOrganization(
            nameMap = mapOf(LanguageOption.PL to "KN Larp AGH", LanguageOption.EN to "KN Larp AGH"),
            logoImageFile = getFile("logo-larp.png"),
            backgroundImageFile = getFile("bg-larp.jpg"),
            descriptionMap = mapOf(
                LanguageOption.PL to "KN Larp to organizacja studencka zrzeszająca miłośników LARPów (live-action role playing). LARP to wydarzenie z pogranicza gry i przedstawienia teatralnego, podczas którego uczestnicy odgrywają i przeżywają różnego rodzaju historie, osadzone w rozmaitych konwencjach fabularnych. Misją koła jest: organizacja i popularyzacja LARPów, badanie tego zjawiska społecznego oraz tworzenie metod i technologii wspomagających projektowanie LARPów i wzbogacających rozgrywkę. Do współpracy zapraszamy zarówno twórców, doświadczonych graczy, jak i osoby, które nie miały dotychczas styczności z tą formą aktywności. Zachęcamy też do udziału w przygotowanych przez nas wydarzeniach. Chcesz poznać nas lepiej, wziąć udział w naszych wydarzeniach lub dołączyć do zespołu? Skontaktuj się z nami! ",
                LanguageOption.EN to "KN Larp is a student organization bringing together LARP (live-action role playing) enthusiasts. LARP is an event on the border between a game and a theatrical performance, during which participants act out and experience various types of stories set in various plot conventions. The mission of the club is: to organize and popularize LARPs, research this social phenomenon and create methods and technologies supporting the design of LARPs and enriching the gameplay. We invite both creators, experienced players and people who have not had any contact with this form of activity to cooperate with us. We also encourage you to participate in the events we have prepared. Do you want to get to know us better, take part in our events or join the team? Contact us!",
            ),
            leaderEmail = "admin@agh.edu.pl",
        )
        eventService.createEvent(
            organizationId = larp.id!!,
            backgroundImage = getFile("bg-kn-larp-cursed.jpg"),
            nameMap = mapOf(LanguageOption.PL to "Zaklęci", LanguageOption.EN to "Enchanted"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Serdecznie zapraszamy na larpa \"Zaklęci\", który będziemy mieli przyjemność prowadzić już 30.11 na AGH! ⭐\uFE0F\n" +
                    "\n" +
                    "Jest to remix gry \"Zaczarowani\" Jeppe i Maria Bergmann Hamming, w opracowaniu Rafała Matraszka. Pozwala on na kilka godzin przenieść się w ciało mitycznej istoty lub średniowiecznego człowieka i opowiedzieć swoją własną historię \uD83E\uDD0D\uD83D\uDDA4\n",
                LanguageOption.EN to "We cordially invite you to the \"Enchanted\" larp, \u200B\u200Bwhich we will have the pleasure of conducting on November 30 at AGH! ⭐\uFE0F\n" +
                    "\n" +
                    "This is a remix of the game \"Zaczarowani\" by Jeppe and Maria Bergmann Hamming, developed by Rafał Matraszek. It allows you to move into the body of a mythical creature or a medieval man for a few hours and tell your own story   \uD83D\uDDA4",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "AGH A1 322",
                LanguageOption.EN to "AGH A1 322",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(1)),
            endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(1).plusHours(3)),
        )
        eventService.createEvent(
            organizationId = larp.id!!,
            backgroundImage = getFile("bg-kn-larp-eldores.jpg"),
            nameMap = mapOf(LanguageOption.PL to "Eldores", LanguageOption.EN to "Eldores"),
            descriptionMap = mapOf(
                LanguageOption.PL to "Pojawiły się sygnały, że nasz larp jest trochę zbyt tajemniczy - zatem odpowiadamy!\n" +
                    "\n" +
                    "Oto dokument projektowy nakreślający, o czym właściwie będzie Eldoesz. \n" +
                    "\n" +
                    "Mamy nadzieję, że rozwieje to wszelkie wątpliwości i pomoże tym z Was, którzy jeszcze nie zdecydowali się zapisać \uD83D\uDDA4\n" +
                    "\n" +
                    "Design doc: https://drive.google.com/.../15ynNsq1Bs.../view...\n" +
                    "\n" +
                    "Zapisy otwarte będą do 15 sierpnia!",
                LanguageOption.EN to "There have been signals that our LARP is a bit too mysterious - so we are responding!\n" +
                    "\n" +
                    "Here's a design document outlining what Eldoesz will actually be about.\n" +
                    "\n" +
                    "We hope that this will dispel any doubts and help those of you who have not yet decided to sign up \uD83D\uDDA4\n" +
                    "\n" +
                    "Design doc: https://drive.google.com/.../15ynNsq1Bs.../view...\n" +
                    "\n" +
                    "Registration will be open until August 15!",
            ),
            locationMap = mapOf(
                LanguageOption.PL to "AGH B1 122",
                LanguageOption.EN to "AGH B1 122",
            ),
            startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(2)),
            endDate = Timestamp.valueOf(LocalDateTime.now().minusDays(2).plusHours(5)),
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
//        val domainOptions = arrayOf("agh.edu.pl", "student.agh.edu.pl")
//
//        for (i in 500..530) {
//            val emailDomain = domainOptions[i % domainOptions.size]
//            val email = "user$i@$emailDomain"
//
//            authenticationService.register(
//                RegisterRequest(
//                    email = email,
//                    password = "password$i",
//                    firstName = "User",
//                    lastName = "Surname$i",
//                ),
//            )
//        }

        userRepository.findAll().forEach { user ->
            user.apply { enabled = true }
            userRepository.save(user)
        }
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
        imageStorage.saveFile(
            imageService.resize(image, BackgroundImage.BIG_SIZE[0], BackgroundImage.BIG_SIZE[1]),
            imageId,
            "big.$extension",
        )
        imageStorage.saveFile(
            imageService.resize(image, BackgroundImage.MEDIUM_SIZE[0], BackgroundImage.MEDIUM_SIZE[1]),
            imageId,
            "medium.$extension",
        )
        imageStorage.saveFile(
            imageService.resize(image, BackgroundImage.SMALL_SIZE[0], BackgroundImage.SMALL_SIZE[1]),
            imageId,
            "small.$extension",
        )

        return BackgroundImage(imageId, "small.$extension", "medium.$extension", "big.$extension")
    }

    private fun createLogoImage(filename: String): LogoImage {
        val image: BufferedImage = ImageIO.read(File("./mock-images/$filename"))
        val imageId = imageStorage.generateImageId()
        val extension = imageStorage.getFileExtension(filename)
        imageStorage.createImageDirectory(imageId)
        imageStorage.saveFile(
            imageService.resize(image, LogoImage.BIG_SIZE[0], LogoImage.BIG_SIZE[1]),
            imageId,
            "big.$extension",
        )
        imageStorage.saveFile(
            imageService.resize(image, LogoImage.MEDIUM_SIZE[0], LogoImage.MEDIUM_SIZE[1]),
            imageId,
            "medium.$extension",
        )
        imageStorage.saveFile(
            imageService.resize(image, LogoImage.SMALL_SIZE[0], LogoImage.SMALL_SIZE[1]),
            imageId,
            "small.$extension",
        )

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

    private fun assignRoles() {
        val admin = userRepository.findByEmail("admin@agh.edu.pl").get()
        userRepository.findAll().forEach { user ->
            organizationService.assignUserRole(1, user.id!!, OrganizationRole.HEAD)
        }
        admin.role = Role.ADMIN
        userRepository.save(admin)
    }
}
