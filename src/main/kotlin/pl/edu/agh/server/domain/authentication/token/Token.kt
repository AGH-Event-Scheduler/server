package pl.edu.agh.server.domain.authentication.token

import jakarta.persistence.*
import pl.edu.agh.server.domain.user.User

@Entity
class Token(
    @Column(unique = true) val token: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") val user: User,
) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var type: TokenType = TokenType.BEARER

    var revoked: Boolean = false

    var expired: Boolean = false
}
