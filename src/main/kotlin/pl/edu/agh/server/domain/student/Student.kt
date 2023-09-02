package pl.edu.agh.server.domain.student

import jakarta.persistence.Entity
import jakarta.persistence.Table
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.foundation.domain.BasicIdentifiableEntity

@Entity
@Table(name = "STUDENTS")
@ToString
@EqualsAndHashCode(callSuper = true)
class Student(
  var albumNo: String,
  var name: String,
  var surname: String
) : BasicIdentifiableEntity()