package pl.edu.agh.server.foundation.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import pl.edu.agh.server.foundation.domain.BaseRepository

abstract class BaseServiceUtilities<T : BaseIdentifiableEntity>(
    private val repository: BaseRepository<T>,
) {
    protected fun getAllWithSpecificationPageable(
        page: Int,
        size: Int,
        sort: String,
        specification: Specification<T>? = null,
    ): List<T> {
        val sortParams = sort.split(",")
        val sortBy = sortParams[0]
        val sortDirection = if (sortParams.size > 1) Sort.Direction.fromString(sortParams[1]) else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy))

        val entities = if (specification != null) {
            repository.findAll(specification, pageable).content
        } else {
            repository.findAll(pageable).content
        }
        return entities
    }
}
