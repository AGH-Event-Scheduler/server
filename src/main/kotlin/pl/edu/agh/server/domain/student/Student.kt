package pl.edu.agh.server.domain.student

import jakarta.persistence.Entity
import jakarta.persistence.Table
import lombok.Getter
import lombok.Setter
import pl.edu.agh.server.foundation.domain.BasicIdentifiableEntity

@Entity
@Table(name = "STUDENTS")
@Setter
@Getter
class Student(
  private var albumNo: String,
  private var name: String,
  private var surname: String
) : BasicIdentifiableEntity()