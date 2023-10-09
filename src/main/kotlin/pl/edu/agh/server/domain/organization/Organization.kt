package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Organizations")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Organization(
    var name: String,
    var miniatureUrl: String = "https://i.stack.imgur.com/5ykYD.png",
    var imageUrl: String = "https://i.stack.imgur.com/5ykYD.png",
    var isSubscribed: Boolean = false,
    var description: String = "",
    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var events: List<Event> = listOf(),
) : BaseIdentifiableEntity()
