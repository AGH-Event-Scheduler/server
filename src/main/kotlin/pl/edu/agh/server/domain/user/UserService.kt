package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Service
import pl.edu.agh.server.domain.exception.UserNotFoundException

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUserByEmail(userName: String): User {
        return userRepository.findByEmail(userName).orElseThrow { UserNotFoundException(userName) }
    }
}
