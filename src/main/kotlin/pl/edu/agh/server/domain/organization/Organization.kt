package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.LogoImage
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity
import java.util.*

@Entity
@Table(name = "Organization")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Organization(
    var name: UUID,

    @Embedded
    var logoImage: LogoImage,

    @Embedded
    var backgroundImage: BackgroundImage,

    var description: UUID,

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var events: MutableList<Event> = mutableListOf(),

    @ManyToMany(mappedBy = "followedOrganizations")
    var followedByUsers: MutableSet<User> = mutableSetOf(),

) : BaseIdentifiableEntity()
