package pl.edu.agh.server.application.authentication

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.authentication.AuthenticationService

@RestController
@RequestMapping("/api/authentication")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authenticationService.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        var response: AuthenticationResponse? = null
        try {
            response = authenticationService.authenticate(request)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(response)
    }
}
