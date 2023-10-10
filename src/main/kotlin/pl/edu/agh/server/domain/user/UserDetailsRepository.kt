package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository

@Repository
interface UserDetailsRepository : BaseRepository<UserDetails>
