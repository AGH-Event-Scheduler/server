package pl.edu.agh.server.domain.authentication

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository

@Service
class AuthenticationService(private val userRepository: UserRepository) {

    fun authenticate(email: String, password: String): User? {
        val user = userRepository.findByEmail(email)
        if (isPasswordValid(user, password)) {
            return user
        }
        return null
    }

    private fun isPasswordValid(user: User, password: String): Boolean {
        // TODO: implement password hashing
        return user.password == password
    }
}
