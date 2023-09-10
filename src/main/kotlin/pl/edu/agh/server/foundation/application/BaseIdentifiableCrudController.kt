package pl.edu.agh.server.foundation.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import pl.edu.agh.server.foundation.domain.BaseRepository
import javax.validation.Valid

abstract class BaseIdentifiableCrudController<T : BaseIdentifiableEntity>(
    private val repository: BaseRepository<T>,
) : BaseIdentifiableReadController<T>(repository) {

    @PostMapping
    fun create(@Valid @RequestBody entity: T): ResponseEntity<T> {
        val createdEntity = repository.save(entity)
        return ResponseEntity.ok(createdEntity)
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody updatedEntity: T,
    ): ResponseEntity<T> {
        val existingEntity = repository.findById(id)
        if (existingEntity.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        val entityToUpdate = existingEntity.get()

        entityToUpdate.updateFields(updatedEntity)

        val updated = repository.save(entityToUpdate)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }

        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
