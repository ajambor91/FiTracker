package aj.FiTracker.FiTracker.Security;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/users/register", "/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/csrf-token").permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.csrfTokenRepository(new CsrfCookieTokenRepository()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .httpBasic(withDefaults());
        ;

        return http.build();
    }
}
