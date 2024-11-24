package ma.bonmyd.backendincident.repositories.territoriale;

import ma.bonmyd.backendincident.entities.territoriale.Province;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province,Long> {
    @Query("SELECT p FROM Province p WHERE ST_Contains(p.geom, ST_GeomFromText(:location, 4326)) = true")
    Province findProvinceContainingPoint(@Param("location") String wktLocation);

}
