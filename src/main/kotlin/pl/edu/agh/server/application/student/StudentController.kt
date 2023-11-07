package pl.edu.agh.server.application.student

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.student.StudentRepository

@RestController
@RequestMapping("/api/students")
class StudentController(
    private val studentRepository: StudentRepository,
)
