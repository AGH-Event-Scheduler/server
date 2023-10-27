package pl.edu.agh.server.application.authentication

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)
