package pl.edu.agh.server.foundation.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
  fun getAll(
    @RequestParam(name = "page", defaultValue = "0") page: Int,
    @RequestParam(name = "size", defaultValue = "${Integer.MAX_VALUE}") size: Int,
    @RequestParam(name = "sort", defaultValue = "id,asc") sort: String
  ): ResponseEntity<List<T>> {
    val sortParams = sort.split(",")
    val sortBy = sortParams[0]
    val sortDirection = if (sortParams.size > 1) Sort.Direction.fromString(sortParams[1]) else Sort.Direction.ASC
    val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
    val entities = repository.findAll(pageable).content
    return ResponseEntity.ok(entities)
  }
}
