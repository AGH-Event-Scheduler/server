package pl.edu.agh.server.application.authentication

import io.jsonwebtoken.io.IOException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.application.authentication.responseview.VerificationView
import pl.edu.agh.server.domain.authentication.AuthenticationService

@RestController
@RequestMapping("/api/authentication")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Void> {
        authenticationService.register(request)
        return ResponseEntity.ok().build()
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
    fun logout(request: HttpServletRequest, @RequestParam("refreshToken") refreshToken: String): ResponseEntity<Void> {
        authenticationService.logout(refreshToken)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/verify")
    fun verify(@RequestParam("token") verificationToken: String, response: HttpServletResponse) {
        try {
            authenticationService.verifyEmail(verificationToken)
            response.contentType = "text/html"
            response.writer.write(VerificationView.emailVerificationSuccessHTML())
        } catch (e: Exception) {
            response.contentType = "text/html"
            response.writer.write(VerificationView.emailVerificationFailureHTML())
        }
    }

    @PostMapping("/reset-password")
    fun prepareResetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Void> {
        authenticationService.prepareForPasswordChange(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/resend-verification-mail")
    fun resendVerificationMail(@RequestParam email: String): ResponseEntity<Void> {
        authenticationService.resendVerificationEmail(email)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/verify-password")
    fun verifyAndResetPassword(@RequestParam("token") verificationToken: String, response: HttpServletResponse) {
        try {
            authenticationService.resetPasswordAfterVerification(verificationToken)
            response.contentType = "text/html"
            response.writer.write(VerificationView.passwordChangeVerificationSuccessHTML())
        } catch (e: Exception) {
            response.contentType = "text/html"
            response.writer.write(VerificationView.passwordChangeVerificationFailureTML())
        }
    }
}
