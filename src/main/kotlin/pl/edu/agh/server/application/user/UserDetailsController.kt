package pl.edu.agh.server.application.user

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.user.UserDetails
import pl.edu.agh.server.domain.user.UserDetailsRepository
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController

@RestController
@RequestMapping("/api/user-details")
class UserDetailsController(
    private val userDetailsRepository: UserDetailsRepository,
) : BaseIdentifiableCrudController<UserDetails>(userDetailsRepository)
