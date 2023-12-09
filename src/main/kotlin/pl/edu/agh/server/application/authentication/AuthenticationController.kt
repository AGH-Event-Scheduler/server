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

            val htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Email Verification Successful</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            text-align: center;
                            margin: 20vh auto;
                        }

                        h1 {
                            font-size: 2em;
                            color: #0066cc; /* Change the color as desired */
                        }

                        p {
                            font-size: 1.2em;
                            color: #333; /* Change the color as desired */
                        }
                    </style>
                </head>
                <body>
                    <h1>Email Verification Successful</h1>
                    <p>You can now login to your application.</p>
                </body>
                </html>
            """.trimIndent()

            response.contentType = "text/html"
            response.writer.write(htmlContent)
        } catch (e: Exception) {
            response.contentType = "text/plain"
            response.writer.write("Email Verification token is invalid or has expired.")
        }
    }
}
