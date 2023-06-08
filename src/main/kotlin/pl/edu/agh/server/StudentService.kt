package pl.edu.agh.server

import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StudentService(private val studentRepository: StudentRepository) {
    fun saveStudent(student: SaveStudentQuery) {
        val newStudent = Student(student.name)
        studentRepository.save(newStudent)
    }

    fun getStudents(): List<GetStudentsQueryResult> {
        return studentRepository.findAll()
            .stream()
            .map { student -> GetStudentsQueryResult(student.id!!, student.name) }
            .collect(Collectors.toList())
    }
}
