package ma.bonmyd.backendincident.entities.incident;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "types")
@Data
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "type",cascade = CascadeType.ALL)
    private List<Incident> incidents;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_fk")
    private Sector sector;
}
