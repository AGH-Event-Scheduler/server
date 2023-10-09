package pl.edu.agh.server.domain.event

import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.time.LocalDateTime

@Entity
@Table(name = "Event")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Event(
    var name: String,

    @Embedded
    var backgroundImage: BackgroundImage,

    @Column(length = 1000)
    var description: String,

    var startDate: LocalDateTime,

    var endDate: LocalDateTime,

    var location: String,

    @ManyToOne
    @JoinColumn(name = "organization_id")
    var organization: Organization,
) : BaseIdentifiableEntity()
