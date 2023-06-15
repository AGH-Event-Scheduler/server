package pl.edu.agh.server.organization

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.Data

@Entity
@Data
data class Organization(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",

    var imageUrl: String = "https://i.stack.imgur.com/5ykYD.png",

    var description: String = "",

    //TODO move to diffrent class after demo
    var isSubscribed: Boolean
)