package pl.edu.agh.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class SaveStudentQuery(val name: String)
data class GetStudentsQueryResult(val id: Long, val name: String)

@RestController
class StudentController(private val studentService: StudentService) {
    @PostMapping("/student")
    fun saveStudent(@RequestBody student: SaveStudentQuery) {
        studentService.saveStudent(student)
    }

    @GetMapping("/students")
    fun getStudents(): List<GetStudentsQueryResult> {
        return studentService.getStudents()
    }
}