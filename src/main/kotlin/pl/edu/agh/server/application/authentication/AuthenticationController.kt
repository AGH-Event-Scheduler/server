package pl.edu.agh.server.application.authentication

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.authentication.AuthenticationService
import pl.edu.agh.server.domain.user.User

@RestController
@RequestMapping("/api/authentication")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<User> {
        val user = authenticationService.authenticate(loginRequest.email, loginRequest.password)

        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.badRequest().build()
        }
    }
}

data class LoginRequest(
    val email: String,
    val password: String,
)
