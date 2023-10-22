package pl.edu.agh.server.application.authentication

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
