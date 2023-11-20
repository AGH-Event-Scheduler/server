package pl.edu.agh.server.application.user

import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.dto.UserDTO
import pl.edu.agh.server.domain.user.UserService

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
) {
    @GetMapping
    fun getUser(
        @RequestParam(name = "email") email: String,
    ): UserDTO {
        val user = userService.getUserByEmail(email)
        return modelMapper.map(user, UserDTO::class.java)
    }
}
