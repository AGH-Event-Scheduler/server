package pl.edu.agh.server.domain.exception

class UserNotFoundException(userName: String) : RuntimeException("User with email $userName not found")
