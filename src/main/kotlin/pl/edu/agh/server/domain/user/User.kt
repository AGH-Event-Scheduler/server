package pl.edu.agh.server.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import lombok.EqualsAndHashCode
import lombok.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import pl.edu.agh.server.domain.authentication.token.Token
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.notification.Notification
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRole
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "_USER")
@ToString
@EqualsAndHashCode(callSuper = true)
class User(
    @Column(unique = true)
    @Email
    var email: String,

    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private var password: String,

    var name: String,

    var lastName: String,

    @Enumerated(EnumType.STRING)
    var role: Role,

) : BaseIdentifiableEntity(), UserDetails {

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "user_followed_organizations",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "organization_id")],
    )
    val followedOrganizations: MutableSet<Organization> = mutableSetOf()

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "user_saved_events",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id")],
    )
    val savedEvents: MutableSet<Event> = mutableSetOf()

    @ManyToMany(mappedBy = "seenByUsers")
    val seenNotifications: MutableSet<Notification> = mutableSetOf()

    @OneToMany(mappedBy = "user")
    private val tokens: List<Token> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var organizationUserRoles: MutableList<OrganizationUserRole> = mutableListOf()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
