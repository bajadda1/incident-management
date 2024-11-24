package ma.bonmyd.backendincident.entities.users;

import jakarta.persistence.*;
import lombok.Data;
import ma.bonmyd.backendincident.entities.incident.Incident;

import java.util.List;

@Entity
@Data
@Table(name = "citizens")
public class Citizen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //international mobile equipment identity
    private String imei;
    @OneToMany(mappedBy = "citizen",cascade = CascadeType.ALL)
    private List<Incident> incidents;
}
