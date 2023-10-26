package pl.edu.agh.server.application.user

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
) : BaseIdentifiableCrudController<User>(userRepository)
