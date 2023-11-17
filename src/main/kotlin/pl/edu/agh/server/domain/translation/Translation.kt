package pl.edu.agh.server.domain.translation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.util.*

@Entity
@Table(
    name = "Translation",
    uniqueConstraints = [
        UniqueConstraint(
            name = "UniqueTranslationIdAndLanguage",
            columnNames = ["id", "language"],
        ),
    ],
)
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Translation(
    @Column(length = 1000)
    var content: String,
    var language: LanguageOption,
) : BaseIdentifiableEntity()
