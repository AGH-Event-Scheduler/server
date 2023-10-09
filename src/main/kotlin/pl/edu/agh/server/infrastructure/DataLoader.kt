package pl.edu.agh.server.infrastructure

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.common.LogoImage
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import java.time.LocalDateTime

@Configuration
class DataLoader(
    private val studentRepository: StudentRepository,
    private val organizationRepository: OrganizationRepository,
    private val eventRepository: EventRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val student = Student("472924", "Kamil", "Błażewicz")
        studentRepository.save(student)
        createOrganizations()
        createEvents()
    }

    private fun createOrganizations() {
        organizationRepository.saveAll(
            listOf(
                Organization(
                    name = "KN BIT",
                    isSubscribed = false,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/6vvcu0fN9x6damaH2chW1A/b7a014b13b6e355ccce4196712a5b2b4/bit-logo-black.jpg"),
                    backgroundImage = createBackgroundImage("https://scontent-waw1-1.xx.fbcdn.net/v/t39.30808-6/326557612_1115707902439686_3600150035022229767_n.jpg?stp=dst-jpg_p640x640&_nc_cat=102&ccb=1-7&_nc_sid=52f669&_nc_ohc=69lLv7Rm9RcAX9jyR4k&_nc_ht=scontent-waw1-1.xx&oh=00_AfDYu77iHxecnVNVtRYWr_P9RsjAlWl8mK-M1VPyLGFIGQ&oe=652A0932"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "KN Osób Studiujących Socjologię",
                    isSubscribed = true,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/6lDoTg2CZHE6PXtFcMN3QU/ff7406cb993c922d0673217941691700/sygnet_z_podpisem_-na_jasne_t_____o-.png"),
                    backgroundImage = createBackgroundImage("https://scontent-waw1-1.xx.fbcdn.net/v/t39.30808-6/348235559_1854105901637991_5866130343039219612_n.png?stp=dst-png_s960x960&_nc_cat=110&ccb=1-7&_nc_sid=52f669&_nc_ohc=KguFwPyDnkEAX8qaJCg&_nc_ht=scontent-waw1-1.xx&oh=00_AfALQNae7xP6QAGARTfBSCKamVH7tukpC3_uaZdCh7T5gg&oe=6529338C"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "BioMedical Innovations",
                    isSubscribed = true,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/13nZonPMXG0IdiKYepcazY/a75a6f11bc6a4b90d3828acab621e7fa/logo.png"),
                    backgroundImage = createBackgroundImage("https://skn.agh.edu.pl/static/og-image-c72c98f019ad0e2b3a94f73693f8d457.png"),
                    description = shortLoremIpsum(),
                ),
                Organization(
                    name = "Koło Naukowe Creative",
                    isSubscribed = false,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/2k6DY1kwOBQhahKuCbpIvc/bb03660786772eb34283a43d01f06163/creative_____kopia.jpg"),
                    backgroundImage = createBackgroundImage("https://scontent-waw1-1.xx.fbcdn.net/v/t39.30808-6/291327502_537868148034721_130053540757400704_n.png?stp=dst-png_s960x960&_nc_cat=102&ccb=1-7&_nc_sid=52f669&_nc_ohc=Qg89GVzFNBMAX_M7Rah&_nc_ht=scontent-waw1-1.xx&oh=00_AfBVNToMzx-xyBPoVpvCvA2J-K1EqdGU0uyOomBisCB-3g&oe=652854D9"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "AGH Eko-Energia",
                    isSubscribed = false,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/6Clmj70XeHofaHaKs4gk9f/b8cae55e413d98254dc3573e50a50a2c/Logo_kwadrat.jpg"),
                    backgroundImage = createBackgroundImage("https://energiaimy.pl/wp-content/uploads/2016/03/Eko_flat_left_2.png"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "Koło Naukowe Data Team",
                    isSubscribed = true,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/5lCSN4c2tjkX4XrIV0vCB/06b3b3ef735c8e2c0895a3c2284d24d8/338877992_6012870298833520_3324859086898778115_n.jpg"),
                    backgroundImage = createBackgroundImage("https://opengraph.githubassets.com/78d00acc43ccdc2b371896258fd491689e2081f5475d8a7e4cbbfa45ae3067bf/Kolo-Naukowe-Data-Science-PW/Rekrutacja20"),
                    description = shortLoremIpsum(),
                ),
                Organization(
                    name = "KN 4 Society",
                    isSubscribed = true,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/72PtIIc5egzpyEgYvNBVyD/79a9c93385ce66132e2f6cf1ca0dd746/logo.jpeg"),
                    backgroundImage = createBackgroundImage("https://scontent-waw1-1.xx.fbcdn.net/v/t39.30808-6/306754035_461041406064479_2917323435672627171_n.jpg?stp=dst-jpg_s960x960&_nc_cat=110&ccb=1-7&_nc_sid=52f669&_nc_ohc=zy9WEOVp8KQAX9xBFBR&_nc_ht=scontent-waw1-1.xx&oh=00_AfDabdP6bpfteYRum1BLuQgipSirENyJDfrXluRHOTHENA&oe=6528D813"),
                    description = longLoremIpsum(),
                ),
                Organization(
                    name = "KN Energon",
                    isSubscribed = true,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/2hGRA4ipUETBw2l5dpxXFb/a6f465d9ca7566722c19b766f37505f3/LOGO.png"),
                    backgroundImage = createBackgroundImage("http://www.knpg.agh.edu.pl/wp-content/uploads/2020/01/GLY_5139-1024x683.jpg"),
                    description = mediumLoremIpsum(),
                ),
                Organization(
                    name = "KN Larp AGH",
                    isSubscribed = false,
                    logoImage = createLogoImage("https://images.ctfassets.net/hvenzvkwiy9m/5UnCBCqiKVBYXaUPlNGUuf/89ddbcf2160fe22dd6f320b39ff33bf8/logoAGHsygnet.png"),
                    backgroundImage = createBackgroundImage("https://scontent-waw1-1.xx.fbcdn.net/v/t39.30808-6/332236117_207015225310006_7825365902212222981_n.jpg?stp=cp0_dst-jpg_e15_fr_q65&_nc_cat=109&ccb=1-7&_nc_sid=5f2048&_nc_ohc=YkTdhooIeZEAX9J_zHX&_nc_oc=AQkFuFMJ-3lyJxR0cp2vP3q1CqpfB6OE7LlbL7G77sVup5WbsiJ2RFP6IWCECur9E-g0MMpcgOmkILdsf9L9SPlS&_nc_ht=scontent-waw1-1.xx&oh=00_AfBcrUGNv-2Ci96lC1B27wUK7w7bThHr3TFOVyg2a-UhGQ&oe=6528D379"),
                    description = shortLoremIpsum(),
                ),
            ),
        )
    }

    private fun createEvents() {
        var organizations = organizationRepository.findAll()
        for (org: Organization in organizations) {
            var events = listOf(
                Event(
                    name = "Test Event 1",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = shortLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().plusDays(1),
                    endDate = LocalDateTime.now().plusDays(1).plusMinutes(90),
                ),
                Event(
                    name = "Test Event 2",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = longLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().plusDays(2),
                    endDate = LocalDateTime.now().plusDays(2).plusMinutes(90),
                ),
                Event(
                    name = "Test Event 3",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = mediumLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().minusDays(2),
                    endDate = LocalDateTime.now().minusDays(2).plusMinutes(90),
                ),
                Event(
                    name = "Test Event 4",
                    backgroundImage = org.backgroundImage,
                    organization = org,
                    description = longLoremIpsum(),
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().minusDays(3),
                    endDate = LocalDateTime.now().minusDays(3).plusMinutes(120),
                ),
            )

            org.events = events
            eventRepository.saveAll(events)
            organizationRepository.save(org)
        }
    }

    private fun createBackgroundImage(url: String): BackgroundImage {
        return BackgroundImage(url, url, url)
    }

    private fun createLogoImage(url: String): LogoImage {
        return LogoImage(url, url, url)
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
