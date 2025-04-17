package aj.FiTracker.FiTracker.Security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultTransitKey;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@Configuration
public class OAuthConfig {
    private static final Logger lopgger = LoggerFactory.getLogger(OAuthConfig.class);
    private final VaultTransitOperations vaultTransitOperations;
    @Value("${oauth.client.id}")
    private String clientId;
    @Value("${oauth.client.secret}")
    private String clientSecret;

    @Autowired
    public OAuthConfig(VaultTransitOperations vaultTransitOperations) {
        this.vaultTransitOperations = vaultTransitOperations;
        lopgger.info("OAuthConfig initialized with VaultTransitOperations");

    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        lopgger.info("Creating RegisteredClientRepository bean");

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret("{noop}" + clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8080/login/oauth2/code/client-id")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(client);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException, InvalidKeySpecException {
        lopgger.info("Creating JWKSource bean");

        RSAKey rsaKey = generateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        lopgger.info("JWKSet created with key ID: {}", rsaKey.getKeyID());
        lopgger.debug("JWKSet details: {}", jwkSet.toString(false));
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private RSAKey generateRsaKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        lopgger.info("Generating RSA key from Vault.");

        VaultTransitKey transitKey = vaultTransitOperations.getKey("jwt-rsa-key");
        if (transitKey != null) {
            lopgger.debug("Retrieved Vault transit key: {}", transitKey.getName());
        } else {
            lopgger.warn("Failed to retrieve Vault transit key 'jwt-rsa-key'");
        }
        assert transitKey != null;
        String extractedKey = VaultUtils.extractKey(transitKey);
        lopgger.debug("Extracted key from Vault transit key (first few chars): {}", extractedKey.substring(0, Math.min(extractedKey.length(), 50)) + "...");
        RSAPublicKey rsaPublicKey = RSAUtil.getRSAPubKey(extractedKey);
        String kid = RSAUtil.generateKidFromPublicKey(rsaPublicKey);
        lopgger.info("Generated RSA key with key ID (KID): {}", kid);
        lopgger.debug("RSA Public Key details: {}", rsaPublicKey);
        return new RSAKey.Builder(rsaPublicKey)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(kid)
                .algorithm(JWSAlgorithm.PS256)
                .build();
    }
}