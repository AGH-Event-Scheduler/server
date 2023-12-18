package pl.edu.agh.server.domain.authentication.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TokenRepository : JpaRepository<Token, Long> {

    @Query(
        value = " select t from Token t" +
            " inner join User u on t.user.id = u.id " +
            "where u.id = :id and  t.revoked = false and t.category = 'ACCESS'",
    )
    fun findAllValidTokenByUser(id: Long?): List<Token>

    fun findByToken(token: String?): Optional<Token>

    @Query(
        value = "select t from Token t where t.user.id = :userId and t.revoked = false",
    )
    fun findAllByUserId(userId: Long): List<Token>
}
