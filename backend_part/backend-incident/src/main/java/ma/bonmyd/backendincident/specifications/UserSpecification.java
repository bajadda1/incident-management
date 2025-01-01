package ma.bonmyd.backendincident.specifications;

import ma.bonmyd.backendincident.entities.users.User;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {
    public static Specification<User> isEnabled(Boolean enabled) {
        return (root, query, criteriaBuilder) -> enabled != null ? criteriaBuilder.equal(root.get("enabled"), enabled) : null;
    }

    public static Specification<User> hasSectorId(Long sectorId) {
        return (root, query, criteriaBuilder) -> sectorId != null ? criteriaBuilder.equal(root.get("sector").get("id"), sectorId) : null;
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, criteriaBuilder) -> (role != null && !role.isEmpty()) ? criteriaBuilder.equal(root.get("role").get("role"), role) : null;
    }

    public static Specification<User> fullnameContains(String fullname) {
        return (root, query, criteriaBuilder) -> (fullname != null && !fullname.isEmpty()) ? criteriaBuilder.like(root.get("fullname"), "%" + fullname + "%") : null;
    }

    // ||==|> email !
    public static Specification<User> usernameContains(String username) {
        return (root, query, criteriaBuilder) -> (username != null && !username.isEmpty()) ? criteriaBuilder.like(root.get("username"), "%" + username + "%") : null;
    }

}
