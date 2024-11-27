package ma.bonmyd.backendincident.entities.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.bonmyd.backendincident.entities.incident.Sector;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
//@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "_users")
@EntityListeners(AuditingEntityListener.class)

@Getter
@Setter
//@ToString(exclude = {"role", "sector"})

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullname;
    //email unique !
    @Column(name = "email",unique = true, nullable = false)
    private String username;
    private String password;
    //enable professional acc or not
    private boolean enabled;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
    @ManyToOne(cascade = CascadeType.ALL) // Or CascadeType.ALL if appropriate
//    @JsonIgnore // Éviter la sérialisation dans les API REST
    @JoinColumn(name = "role_id")
    private Role role;
    //sector for professional
    @ManyToOne(cascade = CascadeType.ALL)
    private Sector sector;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getRole()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
