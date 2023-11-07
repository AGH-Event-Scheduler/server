package pl.edu.agh.server.domain.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.util.*

@Entity
@Table(name = "Event")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Event(
    var name: UUID,

    @Embedded
    var backgroundImage: BackgroundImage,

    var description: UUID,

    var startDate: Date,

    var endDate: Date,

    var location: UUID,

    @ManyToOne
    @JoinColumn(name = "organization_id")
    var organization: Organization,

    @JsonIgnore
    @ManyToMany(mappedBy = "savedEvents")
    val savedByUsers: MutableSet<User> = mutableSetOf(),

) : BaseIdentifiableEntity()
