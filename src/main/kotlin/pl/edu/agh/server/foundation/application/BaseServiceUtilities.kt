package pl.edu.agh.server.foundation.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import pl.edu.agh.server.foundation.domain.BaseRepository

abstract class BaseServiceUtilities<T : BaseIdentifiableEntity>(
    private val repository: BaseRepository<T>,
) {
    open fun getAllWithSpecificationPageable(
        specification: Specification<T>,
        pageable: PageRequest,
    ): List<T> {
        return repository.findAll(specification, pageable).content
    }

    open fun getAllWithSpecification(
        specification: Specification<T>,
    ): List<T> {
        return repository.findAll(specification)
    }

    open fun getAllWithPageable(
        pageable: PageRequest,
    ): List<T> {
        return repository.findAll(pageable).content
    }

    open fun getAll(): List<T> {
        return repository.findAll()
    }
}
