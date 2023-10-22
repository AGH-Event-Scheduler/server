package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository
import java.util.*

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByEmail(email: String): Optional<User>
}
