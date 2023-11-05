package pl.edu.agh.server.domain.authentication.token

import jakarta.persistence.*
import lombok.AllArgsConstructor
import pl.edu.agh.server.domain.user.User

@Entity
@AllArgsConstructor
class Token(token: String, user: User) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Column(unique = true)
    var token: String? = null

    @Enumerated(EnumType.STRING)
    var type: TokenType = TokenType.BEARER

    var revoked: Boolean = false

    var expired: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null
}
