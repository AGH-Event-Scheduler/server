package pl.edu.agh.server.domain.event

import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository
import java.util.*

@Repository
interface EventRepository : BaseRepository<Event> {

    fun findOrganizationIdById(@Param("id") id: Long): Optional<Long>
}
