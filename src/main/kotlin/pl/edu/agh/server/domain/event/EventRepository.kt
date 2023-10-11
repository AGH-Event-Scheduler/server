package pl.edu.agh.server.domain.event

import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository

@Repository
interface EventRepository : BaseRepository<Event>
