package ma.bonmyd.backendincident.security;


import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

//    @Value("${server.servlet.context-path}")
    private String contextPath="";

    @Value("${auth.api}")
    private String  authApi;

    @Value("${sector.api}")
    private String  sectorApi;

    @Value("${incident.api}")
    private String  incidentApi;

    @Value("${region.api}")
    private String  regionApi;

    @Value("${province.api}")
    private String  provinceApi;

    @Value("${type.api}")
    private String  typeApi;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http.
                cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req.requestMatchers(
                                        contextPath + "/v2/api-docs",
                                        contextPath + "/v3/api-docs",
                                        contextPath + "/v3/api-docs/**",
                                        contextPath + "/swagger-resources",
                                        contextPath + "/swagger-resources/**",
                                        contextPath + "/configuration/ui",
                                        contextPath + "/configuration/security",
                                        contextPath + "/swagger-ui/**",
                                        contextPath + "/webjars/**",
                                        contextPath + "/swagger-ui.html"
                                ) //endpoints without auth
                                .permitAll()
                                .requestMatchers(contextPath + authApi + "/**").permitAll()
                                .requestMatchers(contextPath + sectorApi + "/**").permitAll()
                                .requestMatchers(contextPath + incidentApi + "/**").permitAll()
                                .requestMatchers(contextPath + regionApi + "/**").permitAll()
                                .requestMatchers(contextPath + provinceApi + "/**").permitAll()
                                .requestMatchers(contextPath + typeApi + "/**").permitAll()
//                                .requestMatchers(HttpMethod.POST,contextPath + authApi +"/login").permitAll()

                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                                .anyRequest()  //other endpoints need auth
                                .authenticated()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(this.authenticationProvider)

                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
