package pl.edu.agh.server.foundation.application

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.server.foundation.domain.BasicIdentifiableEntity

@RestController
@RequestMapping("/api")
abstract class BasicIdentifiableReadController<T : BasicIdentifiableEntity>(
  private val repository: JpaRepository<T, Long>
) {
  @GetMapping("/{id}")
  fun getOne(@PathVariable id: Long): ResponseEntity<T> {
    val entity = repository.findById(id)
    return if (entity.isPresent) {
      ResponseEntity.ok(entity.get())
    } else {
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping
  fun getAll(): ResponseEntity<List<T>> {
    val entities = repository.findAll()
    return ResponseEntity.ok(entities)
  }
}
