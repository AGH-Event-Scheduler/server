package pl.edu.agh.server.domain.organization

import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.domain.user.User
import java.util.*

class OrganizationSpecification {
    companion object {
        fun organizationFollowedByUser(userName: String): Specification<Organization> {
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Organization, User>("followedByUsers")
                criteriaBuilder.equal(join.get<String>("email"), userName)
            }
        }
    }
}
