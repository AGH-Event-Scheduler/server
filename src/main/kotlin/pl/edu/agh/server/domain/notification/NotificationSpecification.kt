package pl.edu.agh.server.domain.notification

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.user.Role
import pl.edu.agh.server.domain.user.User

class NotificationSpecification {
    companion object {
        fun notificationForAllUsers(): Specification<Notification> {
            return Specification { root: Root<Notification>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.isTrue(root.get<Boolean>("forAllUsers"))
            }
        }

        fun notificationForAdmins(user: User): Specification<Notification> {
            return Specification { root: Root<Notification>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                if (user.role === Role.ADMIN) {
                    criteriaBuilder.isTrue(root.get<Boolean>("forAdmins"))
                } else {
                    criteriaBuilder.disjunction() // todo verify this works as intended
                }
            }
        }

        fun notificationForUserWithSavedEvents(user: User): Specification<Notification> {
            return Specification { root: Root<Notification>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Notification, Event>("forUsersWithSavedEvents", JoinType.LEFT)
                criteriaBuilder.isMember(user, join.get<MutableSet<User>>("savedByUsers"))
            }
        }

        fun notificationForUserFollowingOrganization(user: User): Specification<Notification> {
            return Specification { root: Root<Notification>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Notification, Organization>("forFollowersOfOrganizations", JoinType.LEFT)
                criteriaBuilder.isMember(user, join.get<MutableSet<User>>("followedByUsers"))
            }
        }

        fun notificationNotSeenByUser(user: User): Specification<Notification> {
            return Specification { root: Root<Notification>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.isNotMember(user, root.get<MutableSet<User>>("seenByUsers"))
            }
        }
    }
}
