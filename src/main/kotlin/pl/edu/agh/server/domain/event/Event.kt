package pl.edu.agh.server.domain.event

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

@Entity
@Table(name = "Event")
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
class Event(
    var name: String,
    var imageUrl: String = "https://i.stack.imgur.com/5ykYD.png",
    var description: String = "",
    @ManyToOne
    @JoinColumn(name = "organization_id")
    var organization: Organization,
) : BaseIdentifiableEntity()
