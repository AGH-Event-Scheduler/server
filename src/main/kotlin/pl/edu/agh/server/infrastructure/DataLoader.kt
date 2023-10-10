package pl.edu.agh.server.infrastructure

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.organization.OrganizationRepository
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import pl.edu.agh.server.domain.user.*

@Configuration
class DataLoader(
    private val studentRepository: StudentRepository,
    private val organizationRepository: OrganizationRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userDetailsService: UserDetailsService,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val student = Student("472924", "Kamil", "Błażewicz")
        studentRepository.save(student)
        createOrganizations()
        createUsers()
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

    private fun createUsers() {
        userRepository.saveAll(
            listOf(
                User(
                    login = "admin",
                    password = "admin",
                ),
                User(
                    login = "user",
                    password = "user",
                ),
            ),
        )
        val adminUser = userService.saveUserDetails(UserDetails(userRepository.findById(1).get(), "Kamil", "Błażewicz"))
        userDetailsService.subscribeToOrganization(adminUser, organizationRepository.findById(1).get())
    }
}
