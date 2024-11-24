package ma.bonmyd.backendincident.entities.incident;

import jakarta.persistence.*;
import lombok.Data;
import ma.bonmyd.backendincident.entities.users.User;

import java.util.List;

@Entity
@Table(name = "sectors")
@Data
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "sector",cascade = CascadeType.ALL)
    private List<Type> types;
}
