package pl.edu.agh.server.domain.notification

import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Notification")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Notification(

    var type: NotificationType,

    @OneToOne(fetch = FetchType.EAGER)
    var regardingEvent: Event? = null,

    @OneToOne(fetch = FetchType.EAGER)
    var regardingOrganization: Organization? = null,

//    For filtering
    @ManyToMany
    var seenByUsers: MutableSet<User> = mutableSetOf(),

    @ManyToMany
    var forFollowersOfOrganizations: MutableSet<Organization> = mutableSetOf(),

    @ManyToMany
    var forWritersOfOrganizations: MutableSet<Organization> = mutableSetOf(),

    @ManyToMany
    var forDirectorsOfOrganizations: MutableSet<Organization> = mutableSetOf(),

    @ManyToMany
    var forUsersWithSavedEvents: MutableSet<Event> = mutableSetOf(),

    var forAdmins: Boolean = true,

    var forAllUsers: Boolean = false,

) : BaseIdentifiableEntity()
