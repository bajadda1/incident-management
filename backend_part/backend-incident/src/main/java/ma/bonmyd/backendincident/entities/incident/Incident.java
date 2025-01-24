package ma.bonmyd.backendincident.entities.incident;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.bonmyd.backendincident.entities.territoriale.Province;
import ma.bonmyd.backendincident.entities.users.Citizen;
import ma.bonmyd.backendincident.enums.Status;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "incidents")
@Data
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photo;
    private Date createdAt;
    //if the user update it after a rejection reason
    private Date updatedAt;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "sector_fk")
    @NotNull(message = "sector could not be null")
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "type_fk")
    @NotNull(message = "type could not be null")
    private Type type;
    //ignore this field in serialization
    //instead jackson will use public getters & setters
    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    @NotNull(message = "location could not be null")
    private Point location;

    @ManyToOne
    @JoinColumn(name = "citizen_fk")
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "province_fk")
    Province province;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "incident",cascade = CascadeType.ALL)
    @OrderBy("date ASC")
    private List<Rejection> rejections;
    // WKT-based getter and setter for location
    public String getLocation() {
        return new WKTWriter().write(location);
    }

    public void setLocation(String wkt) {
        try {
            this.location = (Point) new WKTReader().read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("Invalid WKT format", e);
        }
    }
}
