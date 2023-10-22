package pl.edu.agh.server.domain.authentication

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pl.edu.agh.server.application.authentication.AuthenticationRequest
import pl.edu.agh.server.application.authentication.AuthenticationResponse
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.user.Role
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
) {
    fun register(request: RegisterRequest): AuthenticationResponse {
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.firstName,
            lastName = request.lastName,
            role = Role.USER,
        )
        userRepository.save(user)

        return AuthenticationResponse(jwtService.generateToken(user))
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user = userRepository.findByEmail(request.email).orElseThrow { throw Exception("User not found") }
        return AuthenticationResponse(jwtService.generateToken(user))
    }
}
