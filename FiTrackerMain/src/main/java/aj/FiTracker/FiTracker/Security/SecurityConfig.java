package aj.FiTracker.FiTracker.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Profile("!integration")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring authorization server security filter chain");

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        logger.info("Applied default security settings for OAuth2 Authorization Server");

        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring default security filter chain.");
        http
                .authorizeHttpRequests(auth -> {
                    logger.info("Configuring authorization rules.");
                    auth
                            .requestMatchers(HttpMethod.POST, "/users/register", "/users/login").permitAll();
                    logger.debug("Permitting POST requests to /users/register and /users/login");
                    auth
                            .requestMatchers(HttpMethod.GET, "/core/csrf-token").permitAll();
                    logger.debug("Permitting GET requests to /core/csrf-token");
                    auth
                            .requestMatchers(HttpMethod.GET, "/oauth2/*").permitAll();
                    logger.debug("Permitting GET requests to /oauth2/*");
                    auth
                            .requestMatchers(HttpMethod.GET, "/oauth2/*/**").permitAll();
                    logger.debug("Permitting GET requests to /oauth2/*/**");
                    auth
                            .anyRequest().authenticated();
                    logger.debug("All other requests require authentication");
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

                .csrf(csrf -> {
                    logger.info("Configuring CSRF protection");
                    CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
                    csrf.csrfTokenRepository(csrfTokenRepository);
                    logger.debug("Using CookieCsrfTokenRepository for CSRF token storage");
                });

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JWTDecode.getDecoder();
    }

}
