package aj.FiTracker.FiTracker.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Profile("integrationWithCSRF")
@Configuration
@EnableWebSecurity
public class SecurityConfigWithCSRF {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req -> req.anyRequest().permitAll())
                .csrf(csrf -> {
                    CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
                    csrf.csrfTokenRepository(csrfTokenRepository);
                    CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();
                    csrf.csrfTokenRequestHandler(csrfTokenRequestHandler);
                });
        return http.build();
    }
}
