package pl.edu.agh.server.domain.student

import org.springframework.stereotype.Repository
import pl.edu.agh.server.foundation.domain.BaseRepository

@Repository
interface StudentRepository : BaseRepository<Student>
