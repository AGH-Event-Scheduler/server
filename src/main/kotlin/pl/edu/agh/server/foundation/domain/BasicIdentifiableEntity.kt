package pl.edu.agh.server.foundation.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.Getter
import lombok.Setter
import java.time.LocalDateTime
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

@MappedSuperclass
@Setter
@Getter
open class BasicIdentifiableEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private val id: Long? = null,

  private val creationDate: LocalDateTime = LocalDateTime.now(),

  private var lastUpdatedDate: LocalDateTime = LocalDateTime.now()
) {

  open fun updateFields(entity: BasicIdentifiableEntity) {
    val entityClass = this::class
    val targetClass = entity::class

    entityClass.declaredMemberProperties.forEach { prop ->
      if (prop.name != "id" && prop.name != "creationDate" && prop.name != "lastUpdatedDate") {
        val thisProp = targetClass.memberProperties.find { it.name == prop.name }
        thisProp?.let { targetProp ->
          val entityValue = targetProp.getter.call(entity)
          if (entityValue != null || (targetProp is KMutableProperty<*>)) {
            if (targetProp is KMutableProperty<*>) {
              (targetProp as KMutableProperty<*>).setter.call(this, entityValue)
            }
          }
        }
      }
    }
    updateLastUpdatedDate()
  }

  private fun updateLastUpdatedDate() {
    lastUpdatedDate = LocalDateTime.now()
  }

}