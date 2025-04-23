package aj.FiTracker.FiTracker.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!integration")
public class SecurityConf {

    private final Logger logger = LoggerFactory.getLogger(SecurityConf.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        logger.info("Configuring SecurityFilterChain.");
        HttpSecurity securityFilterChain = httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        logger.info("SecurityFilterChain configured successfully.");
        return securityFilterChain.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        logger.info("Creating JwtDecoder bean.");
        JwtDecoder jwtDecoder = JWTDecode.getDecoder();
        logger.info("JwtDecoder bean created successfully.");
        return jwtDecoder;
    }

}