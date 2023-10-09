package pl.edu.agh.server.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.common.Image
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Organization")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Organization(
    var name: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "smallUrl", column = Column(name = "logoSmallUrl", length = 500)),
        AttributeOverride(name = "mediumUrl", column = Column(name = "logoMediumUrl", length = 500)),
        AttributeOverride(name = "bigUrl", column = Column(name = "logoBigUrl", length = 500)),
    )
    var logoImage: Image,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "smallUrl", column = Column(name = "backgroundSmallUrl", length = 500)),
        AttributeOverride(name = "mediumUrl", column = Column(name = "backgroundMediumUrl", length = 500)),
        AttributeOverride(name = "bigUrl", column = Column(name = "backgroundBigUrl", length = 500)),
    )
    var backgroundImage: Image,

    var isSubscribed: Boolean = false, // TODO: Remove once users are implemented

    @Column(length = 1000)
    var description: String,

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var events: List<Event> = listOf(),
) : BaseIdentifiableEntity()
