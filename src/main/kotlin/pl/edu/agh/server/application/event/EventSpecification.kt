package pl.edu.agh.server.application.event

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import pl.edu.agh.server.domain.event.Event
import pl.edu.agh.server.domain.organization.Organization

class EventSpecification {
    companion object {
        fun eventBelongToOrganization(id: Long): Specification<Event> {
            return Specification { root: Root<Event>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val predicate: Predicate = criteriaBuilder.equal(root.get<Organization>("organization").get<Long>("id"), id)
                query.where(predicate)
                null
            }
        }
    }
}
