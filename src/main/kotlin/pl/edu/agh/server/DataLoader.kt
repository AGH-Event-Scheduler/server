package pl.edu.agh.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader(private val studentRepository: StudentRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val student = Student("test")
        studentRepository.save(student)

        val students = studentRepository.findAll()
        val student1 = students[0]
        student1.name = "test change"
        studentRepository.save(student1)
    }
}