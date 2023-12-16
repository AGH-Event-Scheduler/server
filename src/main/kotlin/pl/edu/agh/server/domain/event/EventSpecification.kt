package pl.edu.agh.server.domain.event

import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.application.event.EventsType
import pl.edu.agh.server.domain.organization.Organization
import pl.edu.agh.server.domain.translation.LanguageOption
import pl.edu.agh.server.domain.translation.Translation
import pl.edu.agh.server.domain.user.User
import java.util.*

class EventSpecification {
    companion object {
        fun eventBelongToOrganization(organizationId: Long): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.equal(root.get<Organization>("organization").get<Long>("id"), organizationId)
            }
        }

        fun eventInDateRange(startDate: Date, endDate: Date): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.or(
                    criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get<Date>("startDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get<Date>("startDate"), endDate),
                    ),
                    criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get<Date>("endDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get<Date>("endDate"), endDate),
                    ),
                )
            }
        }

        fun eventSavedByUser(userName: String): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Event, User>("savedByUsers")
                criteriaBuilder.equal(join.get<String>("email"), userName)
            }
        }

//        FIXME: Do it on userName if possible
        fun eventFromFollowedByUser(user: User): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.and(
                    criteriaBuilder.isMember(user, root.get<Organization>("organization").get<MutableSet<User>>("followedByUsers")),
                )
            }
        }

        fun eventInDateRangeType(
            date: Date,
            type: EventsType,
        ): Specification<Event> {
            return Specification { root, query, criteriaBuilder ->
                criteriaBuilder.and(
                    when (type) {
                        EventsType.UPCOMING ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), date)
                        EventsType.PAST ->
                            criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), date)
                    },
                )
            }
        }

        fun eventWithNameLike(name: String, languageOption: LanguageOption): Specification<Event> {
            val nameLowerCase = name.lowercase(Locale.getDefault())
            println(nameLowerCase)
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Event, Translation>("name", JoinType.INNER)
                join.on(criteriaBuilder.equal(join.get<LanguageOption>("language"), languageOption))
                criteriaBuilder.like(criteriaBuilder.lower(join.get<String>("content")), "%$nameLowerCase%")
            }
        }

        fun eventNotCanceled(): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                criteriaBuilder.isFalse(root.get<Boolean>("canceled"))
            }
        }

        fun organizationNotArchived(): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val join = root.join<Event, Organization>("organization")
                criteriaBuilder.isFalse(join.get<Boolean>("isArchived"))
            }
        }
    }
}
