package pl.edu.agh.server.foundation.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import pl.edu.agh.server.foundation.domain.BaseRepository

@RestController
@RequestMapping("/api")
abstract class BaseIdentifiableReadController<T : BaseIdentifiableEntity>(
    private val repository: BaseRepository<T>,
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
        @RequestParam(name = "sort", defaultValue = "id,asc") sort: String,
    ): ResponseEntity<List<T>> {
        return getAllWithSpecification(page, size, sort)
    }

    protected fun getAllWithSpecification(
        page: Int,
        size: Int,
        sort: String,
        specification: Specification<T>? = null,
    ): ResponseEntity<List<T>> {
        val sortParams = sort.split(",")
        val sortBy = sortParams[0]
        val sortDirection = if (sortParams.size > 1) Sort.Direction.fromString(sortParams[1]) else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy))

        val entities = if (specification != null) {
            repository.findAll(specification, pageable).content
        } else {
            repository.findAll(pageable).content
        }

        return ResponseEntity.ok(entities)
    }
}
