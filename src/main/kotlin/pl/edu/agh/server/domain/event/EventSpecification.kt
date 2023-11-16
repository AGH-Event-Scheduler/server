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
                val predicate: Predicate =
                    criteriaBuilder.equal(root.get<Organization>("organization").get<Long>("id"), organizationId)
                query.where(predicate)
                null
            }
        }

        fun eventInDateRange(startDate: Date, endDate: Date): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val predicate: Predicate = criteriaBuilder.or(
                    criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get<Date>("startDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get<Date>("startDate"), endDate),
                    ),
                    criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get<Date>("endDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get<Date>("endDate"), endDate),
                    ),
                )
                query.where(predicate)
                null
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
                val predicate: Predicate = criteriaBuilder.and(
                    when (type) {
                        EventsType.UPCOMING ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), date)
                        EventsType.PAST ->
                            criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), date)
                    },
                )

                query.where(predicate)
                null
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
    }
}
