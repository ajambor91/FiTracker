package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.VaultTransitKey;


import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

@Component
public class JWTService {
    private final Logger logger;
    private final ObjectMapper objectMapper;
    private final VaultTransitOperations vaultTransitOperations;
    private final long tokenExpiration;
    @Autowired
    public JWTService(
            VaultTransitOperations vaultTransitOperations,
            ObjectMapper objectMapper,
            @Value("${login.expiration}"
                         ) Long tokenExpiration) {
        this.logger = LoggerFactory.getLogger(JWTService.class);
        this.vaultTransitOperations = vaultTransitOperations;
        this.tokenExpiration = tokenExpiration;
        this.objectMapper = objectMapper;
    }

    public User generateToken(User user) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        return this.generateToken(user, tokenExpiration);
    }

    public User generateToken(User user, long expiration) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        logger.info("Generating token for {} user", user.getId());
        VaultTransitKey transitKey = vaultTransitOperations.getKey("jwt-rsa-key");
        logger.info("Fetch transit key for KID generation, for user: {}", user.getId());
        assert transitKey != null;
        String extractedKey = VaultUtils.extractKey(transitKey);
        RSAPublicKey rsaPublicKey = RSAUtil.getRSAPubKey(extractedKey);
        logger.info("Extracted RSA public key for user {}", user.getId());
        String kid = RSAUtil.generateKidFromPublicKey(rsaPublicKey);
        Map<String, Object> header = Map.of(
                "alg", "PS256",
                "typ", "JWT",
                "kid", kid
        );

        Map<String, Object> claims = Map.of(
                "sub", user.getId(),
                "name", user.getName(),
                "iat", System.currentTimeMillis(),
                "exp", System.currentTimeMillis() + expiration
        );
        logger.info("Created JWT payload and claims for user {}", user.getId());

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(
                objectMapper.writeValueAsBytes(header)
        );

        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                objectMapper.writeValueAsBytes(claims)
        );
        logger.info("Encoded token dara for user {}", user.getId());
        String unsignedToken = encodedHeader + "." + encodedPayload;

        String vaultSignature = vaultTransitOperations.sign("jwt-rsa-key", Plaintext.of(unsignedToken))
                .getSignature();
        logger.info("Signed token data for user {}", user.getId());
        String jwtSignature = VaultUtils.convertVaultSignatureToJWT(vaultSignature);
        String signedToken = unsignedToken + "." + jwtSignature;
        user.setJwt(signedToken.trim());
        logger.info("Set signed token for user {}", user.getId());
        return user;
    }
}
