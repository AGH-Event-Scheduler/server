package pl.edu.agh.server.foundation.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.EqualsAndHashCode
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.*

@MappedSuperclass
@EqualsAndHashCode(of = ["id"])
open class BaseIdentifiableEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val creationDate: Date = Date.from(Instant.now()),

    @LastModifiedDate
    var lastUpdatedDate: Date = Date.from(Instant.now()),
)
