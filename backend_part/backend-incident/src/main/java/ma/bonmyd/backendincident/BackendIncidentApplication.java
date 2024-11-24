package ma.bonmyd.backendincident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendIncidentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendIncidentApplication.class, args);
    }

}
