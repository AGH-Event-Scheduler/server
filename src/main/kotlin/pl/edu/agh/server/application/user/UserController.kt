package pl.edu.agh.server.application.user

import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.domain.dto.UserDTO
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.UserService
import pl.edu.agh.server.foundation.application.BaseControllerUtilities

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val jwtService: JwtService,
) : BaseControllerUtilities<User>(jwtService) {
    @GetMapping
    fun getUser(request: HttpServletRequest): UserDTO {
        val user = userService.getUserByEmail(getUserName(request))
        return modelMapper.map(user, UserDTO::class.java)
    }
}
