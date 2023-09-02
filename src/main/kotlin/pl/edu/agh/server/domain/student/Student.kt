package pl.edu.agh.server.domain.student

import jakarta.persistence.Entity
import jakarta.persistence.Table
import pl.edu.agh.server.foundation.domain.BasicIdentifiableEntity

@Entity
@Table(name = "STUDENTS")
class Student(
  var albumNo: String,
  var name: String,
  var surname: String
) : BasicIdentifiableEntity() {

  override fun updateFields(entity: BasicIdentifiableEntity) {
    if (entity is Student) {
      this.albumNo = entity.albumNo
      this.name = entity.name
      this.surname = entity.surname
    }
    super.updateFields(entity)
  }
}