package pl.edu.agh.server.application.authentication

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)
