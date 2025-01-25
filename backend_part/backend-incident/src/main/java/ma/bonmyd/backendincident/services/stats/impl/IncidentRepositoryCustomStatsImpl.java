package ma.bonmyd.backendincident.services.stats.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ma.bonmyd.backendincident.dtos.incident.IncidentStatusGroupDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncidentRepositoryCustomStatsImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<IncidentStatusGroupDTO> findGroupedByStatus(Specification<Incident> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentStatusGroupDTO> query = cb.createQuery(IncidentStatusGroupDTO.class);
        Root<Incident> root = query.from(Incident.class);

        // Projection: Select status and count
        query.select(cb.construct(
                IncidentStatusGroupDTO.class,
                root.get("status"),
                cb.count(root)
        ));

        // Apply Specification filters
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                query.where(specPredicate);
            }
        }

        // Group by status
        query.groupBy(root.get("status"));

        return entityManager.createQuery(query).getResultList();
    }
}
