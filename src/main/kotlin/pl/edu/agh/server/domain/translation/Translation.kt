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
            columnNames = ["translation_id", "language"],
        ),
    ],
)
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Translation(
    @Column(name = "translation_id")
    var translationId: UUID,
    var content: String,
    var language: LanguageOption,
) : BaseIdentifiableEntity()
