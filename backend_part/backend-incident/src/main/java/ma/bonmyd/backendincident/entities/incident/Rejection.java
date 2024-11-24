package ma.bonmyd.backendincident.entities.incident;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "rejections")
@Data
public class Rejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String reason;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "incident_id")
    private Incident incident;
}
