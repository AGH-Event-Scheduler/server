package pl.edu.agh.server.domain.notification

import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository

@Repository
interface NotificationRepository : BaseRepository<Notification>
