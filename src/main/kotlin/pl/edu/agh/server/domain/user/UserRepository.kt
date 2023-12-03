package pl.edu.agh.server.domain.user

import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository
import java.util.*

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByEmail(email: String): Optional<User>

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    fun findIdByEmail(email: String): Optional<Long>
    fun existsByEmail(email: String): Boolean
}
