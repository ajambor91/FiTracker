package aj.FiTracker.FiTracker.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
public class OAuthServer {
    private final static Logger loggger = LoggerFactory.getLogger(OAuthServer.class);
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        loggger.info("Building AuthorizationServer");
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080").authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .jwkSetEndpoint("/oauth2/jwks")
                .build();
    }
}
