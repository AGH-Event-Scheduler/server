package pl.edu.agh.server.infrastructure

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import pl.edu.agh.server.domain.student.Student
import pl.edu.agh.server.domain.student.StudentRepository

@Configuration
class DataLoader(private val studentRepository: StudentRepository) : CommandLineRunner {
  override fun run(vararg args: String?) {
    val student = Student("472924", "Kamil", "Błażewicz")
    studentRepository.save(student)
  }
}