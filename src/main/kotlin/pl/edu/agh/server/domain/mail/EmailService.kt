package pl.edu.agh.server.domain.mail

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
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

    fun sendEmail(to: String, subject: String, message: String) {
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

    fun sendEmailVerificationMail(to: String, verificationToken: String) {
        val verificationLink = "http://$ip:$port/api/authentication/verify?token=$verificationToken"
        sendEmail(to, "Account Verification", "Click link to verify account: $verificationLink")
    }

    fun sendPasswordChangeVerificationMail(to: String, verificationToken: String) {
        val verificationLink = "http://$ip:$port/api/authentication/verify-password?token=$verificationToken"
        sendEmail(to, "Password Change", "Click link to activate new password: $verificationLink")
    }
}
