package pl.edu.agh.server.foundation.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.EqualsAndHashCode
import java.time.LocalDateTime
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

@MappedSuperclass
@EqualsAndHashCode(of = ["id"])
open class BasedentifiableEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  val creationDate: LocalDateTime = LocalDateTime.now(),

  var lastUpdatedDate: LocalDateTime = LocalDateTime.now()
) {

  open fun updateFields(entity: BasedentifiableEntity) {
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