package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.image.BackgroundImage
import pl.edu.agh.server.domain.image.LogoImage
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

    @Column(length = 1000)
    var description: String,

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var events: MutableList<Event> = mutableListOf(),

) : BaseIdentifiableEntity()
