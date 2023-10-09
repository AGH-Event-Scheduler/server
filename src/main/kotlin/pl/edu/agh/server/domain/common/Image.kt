package pl.edu.agh.server.domain.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import lombok.Data
import lombok.ToString

@Embeddable
@Data
@ToString
class Image(
    @Column(length = 500)
    var smallUrl: String,
    @Column(length = 500)
    var mediumUrl: String,
    @Column(length = 500)
    var bigUrl: String,
)
