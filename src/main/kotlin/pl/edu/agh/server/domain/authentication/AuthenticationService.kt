package pl.edu.agh.server.domain.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pl.edu.agh.server.application.authentication.AuthenticationRequest
import pl.edu.agh.server.application.authentication.AuthenticationResponse
import pl.edu.agh.server.application.authentication.RegisterRequest
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.authentication.token.Token
import pl.edu.agh.server.domain.authentication.token.TokenRepository
import pl.edu.agh.server.domain.user.Role
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import java.io.IOException

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val tokenRepository: TokenRepository,
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
        val accessToken = jwtService.generateToken(user)
        saveUserToken(user, accessToken)
        return AuthenticationResponse(accessToken, jwtService.generateRefreshToken(user))
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user = userRepository.findByEmail(request.email).orElseThrow { throw Exception("User not found") }
        revokeAllUserTokens(user.id!!)
        val accessToken = jwtService.generateToken(user)
        saveUserToken(user, accessToken)
        return AuthenticationResponse(accessToken, jwtService.generateRefreshToken(user))
    }

    @Throws(IOException::class)
    fun refresh(request: HttpServletRequest, response: HttpServletResponse) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }

        val refreshToken: String = authHeader.substring(7)
        val username: String = jwtService.extractUsername(refreshToken)
        val user: User = userRepository.findByEmail(username).orElseThrow()
        if (jwtService.isTokenValid(refreshToken, user)) {
            val accessToken = jwtService.generateToken(user)
            revokeAllUserTokens(user.id!!)
            saveUserToken(user, accessToken)
            val authenticationResponse = AuthenticationResponse(accessToken, jwtService.generateRefreshToken(user))
            ObjectMapper().writeValue(response.outputStream, authenticationResponse)
        }
    }

    private fun saveUserToken(user: User, jwtToken: String) {
        val token: Token = Token(user = user, token = jwtToken)
        tokenRepository.save(token)
    }

    private fun revokeAllUserTokens(userId: Long) {
        val validUserTokens: List<Token> = tokenRepository.findAllValidTokenByUser(userId)
        if (validUserTokens.isEmpty()) return
        validUserTokens.forEach {
            it.apply {
                expired = true
                revoked = true
            }
        }
        tokenRepository.saveAll(validUserTokens)
    }
}
