package pl.edu.agh.server.domain.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.util.*

@Entity
@Table(name = "Event")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Event(
    @OneToMany(fetch = FetchType.EAGER)
    var name: MutableSet<Translation>,

    @Embedded
    var backgroundImage: BackgroundImage,

    @OneToMany(fetch = FetchType.EAGER)
    var description: MutableSet<Translation>,

    var startDate: Date,

    var endDate: Date,

    var canceled: Boolean = false,

    @OneToMany(fetch = FetchType.EAGER)
    var location: MutableSet<Translation>,

    @ManyToOne
    @JoinColumn(name = "organization_id")
    var organization: Organization,

    @JsonIgnore
    @ManyToMany(mappedBy = "savedEvents")
    val savedByUsers: MutableSet<User> = mutableSetOf(),

) : BaseIdentifiableEntity()
