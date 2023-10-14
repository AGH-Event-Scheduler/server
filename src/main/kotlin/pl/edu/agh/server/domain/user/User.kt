package pl.edu.agh.server.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "USERS")
@ToString
@EqualsAndHashCode(callSuper = true)
class User(
    @Column(unique = true)
    @Email
    var email: String,

    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    var password: String,
) : BaseIdentifiableEntity() {

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var userDetails: UserDetails? = null
}
