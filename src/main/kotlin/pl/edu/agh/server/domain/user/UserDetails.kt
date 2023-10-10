package pl.edu.agh.server.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@ToString
@Table(name = "USER_DETAILS")
@EqualsAndHashCode(callSuper = true)
class UserDetails(
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,
    var name: String,
    var surname: String,
) : BaseIdentifiableEntity() {

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    var savedOrganizations: MutableSet<Organization> = mutableSetOf()

    fun subscribeToOrganization(organization: Organization) {
        savedOrganizations.add(organization)
    }

    fun unSubscribeFromOrganization(organization: Organization) {
        savedOrganizations.remove(organization)
    }
}
