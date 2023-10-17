package pl.edu.agh.server.domain.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository

@Repository
interface UserDetailsRepository : BaseRepository<UserDetails> {

    @Query("SELECT ud FROM UserDetails ud WHERE ud.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): UserDetails
}
