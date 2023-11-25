package pl.edu.agh.server.domain.annotation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class AuthorizationException(message: String) : RuntimeException(message)
