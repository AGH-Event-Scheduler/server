package pl.edu.agh.server.domain.organization

import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.user.User
import pl.edu.agh.server.domain.user.organizationroles.OrganizationUserRole
import java.util.*

class OrganizationSpecification {
    companion object {
        fun organizationFollowedByUser(userName: String): Specification<Organization> {
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Organization, User>("followedByUsers")
                criteriaBuilder.equal(join.get<String>("email"), userName)
            }
        }

        fun organizationIsNotArchived(): Specification<Organization> {
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.isFalse(root.get<Boolean>("isArchived"))
            }
        }

        fun organizationIsArchived(): Specification<Organization> {
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.isTrue(root.get<Boolean>("isArchived"))
            }
        }

        fun organizationsWithAuthority(userName: String): Specification<Organization> {
            return Specification { root, query, cb ->
                val joinUserRoles: Join<Organization, OrganizationUserRole> =
                    root.join("organizationUserRoles", JoinType.LEFT)

                val joinUser: Join<OrganizationUserRole, User> =
                    joinUserRoles.join("user", JoinType.LEFT)

                val predicate: Predicate = cb.and(
                    cb.equal(joinUser.get<String>("email"), userName),
                    cb.isNotNull(joinUserRoles.get<Any>("organization")),
                )

                query.distinct(true)

                predicate
            }
        }

        fun organizationWithNameLike(name: String, languageOption: LanguageOption): Specification<Organization> {
//            TODO: update this function once translations are done
            val nameLowerCase = name.lowercase(Locale.getDefault())
            return Specification { root: Root<Organization>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Organization, Translation>("name", JoinType.INNER)
                join.on(criteriaBuilder.equal(join.get<LanguageOption>("language"), languageOption))
                criteriaBuilder.like(criteriaBuilder.lower(join.get<String>("content")), "%$nameLowerCase%")
            }
        }
    }
}
