package ma.bonmyd.backendincident.entities.territoriale;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import ma.bonmyd.backendincident.entities.incident.Incident;
import org.hibernate.annotations.Immutable;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.util.List;

@Entity
@Table(name = "provinces")
@Data
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double area;
    private double perimeter;
    @Column(columnDefinition = "geometry(Polygon, 4326)")
    @JsonIgnore
    private Polygon geom;
    @ManyToOne
    @JoinColumn(name = "region_fk")
    private Region region;

    @OneToMany(mappedBy = "province")
    private List<Incident> incidents;

    public String getGeom() {
        return new WKTWriter().write(geom);
    }

    public void setGeom(String wkt) {
        try {
            this.geom = (Polygon) new WKTReader().read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("Invalid WKT format", e);
        }
    }
}
