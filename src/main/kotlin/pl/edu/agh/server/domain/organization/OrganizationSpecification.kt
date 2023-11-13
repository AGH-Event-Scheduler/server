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

        fun organizationWithNameLike(name: String): Specification<Organization> {
//            TODO: update this function once translations are done
            val nameLowerCase = name.lowercase(Locale.getDefault())
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get<String>("name")), "%$nameLowerCase%")
            }
        }
    }
}
