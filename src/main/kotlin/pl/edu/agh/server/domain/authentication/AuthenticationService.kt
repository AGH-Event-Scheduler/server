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
import pl.edu.agh.server.domain.authentication.token.TokenCategory
import pl.edu.agh.server.domain.authentication.token.TokenRepository
import pl.edu.agh.server.domain.user.Role
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserRepository
import java.io.IOException
import java.util.*

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
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(user, accessToken, TokenCategory.ACCESS)
        saveUserToken(user, refreshToken, TokenCategory.REFRESH)
        return AuthenticationResponse(accessToken, refreshToken)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user = userRepository.findByEmail(request.email).orElseThrow { throw Exception("User not found") }
        revokeAllUserAccessTokens(user.id!!)
        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(user, accessToken, TokenCategory.ACCESS)
        saveUserToken(user, refreshToken, TokenCategory.REFRESH)
        return AuthenticationResponse(accessToken, refreshToken)
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
            val newRefreshToken = jwtService.generateRefreshToken(user)
            saveUserToken(user, accessToken, TokenCategory.ACCESS)
            saveUserToken(user, newRefreshToken, TokenCategory.REFRESH)
            revokeRefreshToken(refreshToken)
            val authenticationResponse = AuthenticationResponse(accessToken, newRefreshToken)
            ObjectMapper().writeValue(response.outputStream, authenticationResponse)
        }
    }

    private fun saveUserToken(user: User, jwtToken: String, category: TokenCategory) {
        val token = Token(user = user, token = jwtToken, category = category)
        tokenRepository.save(token)
    }

    private fun revokeAllUserAccessTokens(userId: Long) {
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

    private fun revokeRefreshToken(token: String) {
        val refreshToken: Optional<Token> = tokenRepository.findByToken(token)
        if (!refreshToken.isPresent) return
        refreshToken.get().apply {
            revoked = true
            expired = true
        }
        tokenRepository.save(refreshToken.get())
    }

    fun logout(refreshToken: String) {
        val user = userRepository.findByEmail(jwtService.extractUsername(refreshToken))
            .orElseThrow { throw Exception("User not found") }
        revokeAllUserAccessTokens(user.id!!)
        revokeRefreshToken(refreshToken)
    }
}
