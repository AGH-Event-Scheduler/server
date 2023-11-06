package pl.edu.agh.server.domain.translation

import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.util.UUID

@Entity
@Table(name = "Translation")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Translation(
    var translationId: UUID,
    var content: String,
    var language: LanguageOption
) : BaseIdentifiableEntity()
