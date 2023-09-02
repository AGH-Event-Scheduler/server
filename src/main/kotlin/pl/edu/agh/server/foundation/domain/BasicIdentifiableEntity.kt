package pl.edu.agh.server.foundation.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
open class BasicIdentifiableEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null,

  var creationDate: LocalDateTime? = null,

  var lastUpdatedDate: LocalDateTime? = null
) {
  init {
    val now = LocalDateTime.now()
    creationDate = now
    lastUpdatedDate = now
  }

  open fun updateFields(entity: BasicIdentifiableEntity) {
    this.lastUpdatedDate = entity.lastUpdatedDate
  }
}