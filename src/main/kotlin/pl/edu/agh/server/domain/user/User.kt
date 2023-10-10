package pl.edu.agh.server.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "USERS")
@ToString
@EqualsAndHashCode(callSuper = true)
class User(
    @Column(unique = true)
    var login: String,
    var password: String,
) : BaseIdentifiableEntity() {

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var userDetails: UserDetails? = null
}
