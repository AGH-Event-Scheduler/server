package pl.edu.agh.server.application.authentication

import io.jsonwebtoken.io.IOException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/refresh")
    @Throws(IOException::class)
    fun refresh(request: HttpServletRequest, response: HttpServletResponse) {
        authenticationService.refresh(request, response)
    }

    @PostMapping("/logout")
    @Throws(IOException::class)
    fun logout(request: HttpServletRequest, @RequestParam("refreshToken") refreshToken: String): ResponseEntity<Void> {
        authenticationService.logout(refreshToken)
        return ResponseEntity.ok().build()
    }
}
