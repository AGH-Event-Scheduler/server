package pl.edu.agh.server.domain.student

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface StudentRepository : JpaRepository<Student, Long>