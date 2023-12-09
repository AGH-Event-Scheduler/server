package pl.edu.agh.server.domain.mail

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EmailService(
    @Value("\${application.email.username}")
    private val emailUsername: String,

    @Value("\${application.email.password}")
    private val emailPassword: String,
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

        email.send()
    }
}
