package pl.edu.agh.server.domain.authentication.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TokenRepository : JpaRepository<Token, Long> {

    @Query(
        value = " select t from Token t" +
            " inner join User u on t.user.id = u.id " +
            "where u.id = :id and (t.expired = false or t.revoked = false)",
    )
    fun findAllValidTokenByUser(id: Long?): List<Token>

    fun findByToken(token: String?): Optional<Token>
}
