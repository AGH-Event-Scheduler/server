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
import pl.edu.agh.server.domain.organization.Organization
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
        name = "user_organizations",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "organization_id")],
    )
    val organizations: MutableSet<Organization> = mutableSetOf()

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