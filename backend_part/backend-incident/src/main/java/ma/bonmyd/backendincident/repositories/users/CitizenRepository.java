package ma.bonmyd.backendincident.repositories.users;

import ma.bonmyd.backendincident.entities.users.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen,Long> {
    Optional<Citizen> findByImei(String imei);
}
