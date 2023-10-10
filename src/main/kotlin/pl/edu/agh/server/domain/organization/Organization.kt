package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.user.UserDetails
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Organizations")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Organization(
    var name: String,
    var imageUrl: String = "https://i.stack.imgur.com/5ykYD.png",
    var isSubscribed: Boolean = false,
    var description: String = "",
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
