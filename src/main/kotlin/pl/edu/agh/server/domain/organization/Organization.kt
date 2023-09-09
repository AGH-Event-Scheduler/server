package pl.edu.agh.server.domain.organization

import jakarta.persistence.Entity
import jakarta.persistence.Table
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
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
) : BaseIdentifiableEntity()
