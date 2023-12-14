package pl.edu.agh.server.application.authentication

data class ResetPasswordRequest(
    val email: String,
    val password: String,
)
