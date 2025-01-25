package ma.bonmyd.backendincident.specifications;

import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public class IncidentSpecification {
    public static Specification<Incident> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> status != null ? criteriaBuilder.equal(root.get("status"), status) : null;

    }

    public static Specification<Incident> hasSectorId(Long sectorId) {
        return (root, query, criteriaBuilder) -> sectorId != null ? criteriaBuilder.equal(root.get("sector").get("id"), sectorId) : null;
    }

    public static Specification<Incident> hasProvinceId(Long provinceId) {
        return (root, query, criteriaBuilder) -> provinceId != null ? criteriaBuilder.equal(root.get("province").get("id"), provinceId) : null;
    }

    public static Specification<Incident> hasRegionId(Long regionId) {
        return (root, query, criteriaBuilder) -> regionId != null ? criteriaBuilder.equal(root.get("province").get("region").get("id"), regionId) : null;
    }


    public static Specification<Incident> hasTypeId(Long typeId) {
        return (root, query, criteriaBuilder) -> typeId != null ? criteriaBuilder.equal(root.get("type").get("id"), typeId) : null;
    }

    public static Specification<Incident> hasDate(Date date) {
        return (root, query, criteriaBuilder) -> date != null ? criteriaBuilder.equal(root.get("createdAt"), date) : null;
    }

    public static Specification<Incident> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> (description != null && !description.isEmpty()) ? criteriaBuilder.like(root.get("description"), "%" + description + "%") : null;
    }

    public static Specification<Incident> hasAllowedStatuses(List<Status> statuses) {
        return (root, query, criteriaBuilder) -> root.get("status").in(statuses);

    }

    public static Specification<Incident> hasCitizenImei(String imei) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("citizen").get("imei"), imei);
    }

    public static Specification<Incident> hasDateBetween(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            } else {
                return null; // No filter applied
            }
        };
    }

}
