package pl.edu.agh.server.domain.mail

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.authentication.token.VerificationTokenType
import java.util.logging.Logger

@Service
class EmailService(
    @Value("\${application.email.username}")
    private val emailUsername: String,

    @Value("\${application.email.password}")
    private val emailPassword: String,

    @Value("\${application.ip}")
    private val ip: String,

    @Value("\${application.port}")
    private val port: String,
) {

    fun sendVerificationMail(to: String, verificationToken: String, verificationTokenType: VerificationTokenType) {
        when (verificationTokenType) {
            VerificationTokenType.EMAIL_VERIFICATION -> sendEmailVerificationMail(to, verificationToken)
            VerificationTokenType.PASSWORD_RESET -> sendPasswordChangeVerificationMail(to, verificationToken)
        }
    }

    private fun sendEmail(to: String, subject: String, message: String) {
        val email: Email = SimpleEmail().apply {
            hostName = "smtp.gmail.com"
            setSmtpPort(587)
            setAuthenticator(DefaultAuthenticator(emailUsername, emailPassword))
            isStartTLSEnabled = true
            setFrom(emailUsername)
            setSubject(subject)
            setMsg(message)
            addTo(to)
        }
        Logger.getAnonymousLogger().info("Sending email to $to")
        email.send()
    }

    private fun generateVerificationLink(tokenType: String, port: String): String {
        val baseUrl = "http://$ip${if (port.isNotBlank()) ":$port" else ""}/api/authentication/"

        return when (tokenType) {
            "verify" -> "${baseUrl}verify?token="
            "verify-password" -> "${baseUrl}verify-password?token="
            else -> throw IllegalArgumentException("Unsupported verification token type: $tokenType")
        }
    }

    private fun sendEmailVerificationMail(to: String, verificationToken: String) {
        val verificationLink = generateVerificationLink("verify", port)
        sendEmail(to, "Account Verification", "Click link to verify account: $verificationLink$verificationToken")
    }

    private fun sendPasswordChangeVerificationMail(to: String, verificationToken: String) {
        val verificationLink = generateVerificationLink("verify-password", port)
        sendEmail(to, "Password Change", "Click link to activate new password: $verificationLink$verificationToken")
    }
}
