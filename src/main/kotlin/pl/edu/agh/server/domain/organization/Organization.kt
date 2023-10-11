package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.common.BackgroundImage
import pl.edu.agh.server.domain.common.LogoImage
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.user.UserDetails
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Organization")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Organization(
    var name: String,

    @Embedded
    var logoImage: LogoImage,

    @Embedded
    var backgroundImage: BackgroundImage,

    var isSubscribed: Boolean = false, // TODO: Remove once users are implemented

    @Column(length = 1000)
    var description: String,

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var events: List<Event> = listOf(),
) : BaseIdentifiableEntity() {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_details_id")
    @JsonIgnore
    var userDetails: MutableSet<UserDetails> = mutableSetOf()

    fun addSubscriber(user: UserDetails) {
        userDetails.add(user)
    }

    fun removeSubscriber(user: UserDetails) {
        userDetails.remove(user)
    }
}
