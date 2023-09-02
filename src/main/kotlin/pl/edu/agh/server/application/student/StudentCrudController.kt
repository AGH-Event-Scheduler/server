package pl.edu.agh.server.application.student

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository
import pl.edu.agh.server.foundation.application.BaseIdentifiableCrudController

@RestController
@RequestMapping("/api/students")
class StudentCrudController(
  private val studentRepository: StudentRepository
) : BaseIdentifiableCrudController<Student>(studentRepository)
