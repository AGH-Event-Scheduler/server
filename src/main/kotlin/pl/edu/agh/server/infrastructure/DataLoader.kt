package pl.edu.agh.server.infrastructure

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.event.EventRepository
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import java.time.LocalDateTime
import java.util.*

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
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/6vvcu0fN9x6damaH2chW1A/b7a014b13b6e355ccce4196712a5b2b4/bit-logo-black.jpg",
                ),
                Organization(
                    name = "KN Osób Studiujących Socjologię",
                    isSubscribed = true,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/6lDoTg2CZHE6PXtFcMN3QU/ff7406cb993c922d0673217941691700/sygnet_z_podpisem_-na_jasne_t_____o-.png",
                ),
                Organization(
                    name = "BioMedical Innovations",
                    isSubscribed = true,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/13nZonPMXG0IdiKYepcazY/a75a6f11bc6a4b90d3828acab621e7fa/logo.png",
                ),
                Organization(
                    name = "Koło Naukowe Creative",
                    isSubscribed = false,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/2k6DY1kwOBQhahKuCbpIvc/bb03660786772eb34283a43d01f06163/creative_____kopia.jpg",
                ),
                Organization(
                    name = "AGH Eko-Energia",
                    isSubscribed = false,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/6Clmj70XeHofaHaKs4gk9f/b8cae55e413d98254dc3573e50a50a2c/Logo_kwadrat.jpg",
                ),
                Organization(
                    name = "Koło Naukowe Data Team",
                    isSubscribed = true,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/5lCSN4c2tjkX4XrIV0vCB/06b3b3ef735c8e2c0895a3c2284d24d8/338877992_6012870298833520_3324859086898778115_n.jpg",
                ),
                Organization(
                    name = "KN 4 Society",
                    isSubscribed = true,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/72PtIIc5egzpyEgYvNBVyD/79a9c93385ce66132e2f6cf1ca0dd746/logo.jpeg",
                ),
                Organization(
                    name = "KN Energon",
                    isSubscribed = true,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/2hGRA4ipUETBw2l5dpxXFb/a6f465d9ca7566722c19b766f37505f3/LOGO.png",
                ),
                Organization(
                    name = "KN Larp AGH",
                    isSubscribed = false,
                    imageUrl = "https://images.ctfassets.net/hvenzvkwiy9m/5UnCBCqiKVBYXaUPlNGUuf/89ddbcf2160fe22dd6f320b39ff33bf8/logoAGHsygnet.png",
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
                    imageUrl = org.imageUrl,
                    organization = org,
                    description = "Test event description 1",
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().plusDays(1),
                    endDate = LocalDateTime.now().plusDays(1).plusMinutes(90),
                ),
                Event(
                    name = "Test Event 1",
                    imageUrl = org.imageUrl,
                    organization = org,
                    description = "Test event description 2",
                    location = "AGH D17 4.26",
                    startDate = LocalDateTime.now().plusDays(2),
                    endDate = LocalDateTime.now().plusDays(2).plusMinutes(90),
                ),
            )

            org.events = events
            eventRepository.saveAll(events)
            organizationRepository.save(org)
        }
    }
}
