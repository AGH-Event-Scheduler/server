package pl.edu.agh.server.domain.notification

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.organization.Organization
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
    var forFollowersOfOrganizations: Set<Organization> = mutableSetOf(),

    @ManyToMany
    var forWritersOfOrganizations: Set<Organization> = mutableSetOf(),

    @ManyToMany
    var forDirectorsOfOrganizations: Set<Organization> = mutableSetOf(),

    @ManyToMany
    var forUsersWithSavedEvents: Set<Event> = mutableSetOf(),

    var forAdmins: Boolean = true,

    var forAllUsers: Boolean = false,

) : BaseIdentifiableEntity()
