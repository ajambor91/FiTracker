package aj.FiTracker.FiTracker.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class JWTDecode implements JwtDecoder {
    private static JWTDecode jwtDecode;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final Logger logger = LoggerFactory.getLogger(JWTDecode.class);
    private final String SECURITY_ALGORITHM = "SHA256withRSAandMGF1";
    private final String PROVIDER = "BC";
    private final String MD_NAME = "SHA-256";
    private final String MGF_NAME = "MGF1";
    private final int SALT_LEN = 478;
    private final int TRAILER = 1;
    private final ObjectMapper objectMapper;
    private final Duration cacheDuration = Duration.ofHours(1);
    @Value("${jwks.api.uri}")
    private String jwksUri;

    private JWTDecode() {
        logger.info("Initializing JWTDecode.");
        this.objectMapper = new ObjectMapper();
        logger.debug("ObjectMapper initialized.");
    }

    public static JWTDecode getDecoder() {
        if (jwtDecode == null) {
            JWTDecode.jwtDecode = new JWTDecode();

        }
        return JWTDecode.jwtDecode;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        logger.info("Decoding JWT token.");
        logger.debug("JWT Token: {}", token);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            logger.debug("JWT parsed successfully. Header: {}, Payload: {}", signedJWT.getHeader().toJSONObject(), signedJWT.getPayload().toJSONObject());
            RSAPublicKey rsaPublicKey = createRSAKey(signedJWT);
            logger.debug("RSA Public Key created.");
            Signature verifier = Signature.getInstance(SECURITY_ALGORITHM, PROVIDER);
            logger.debug("Signature instance created with algorithm: {} and provider: {}.", SECURITY_ALGORITHM, PROVIDER);
            verifier.initVerify(rsaPublicKey);
            logger.debug("Verifier initialized with RSA Public Key.");
            verifier.setParameter(new PSSParameterSpec(
                    MD_NAME,
                    MGF_NAME,
                    MGF1ParameterSpec.SHA256,
                    SALT_LEN,
                    TRAILER
            ));
            logger.debug("Verifier parameters set.");
            byte[] signingInput = (signedJWT.getHeader().toBase64URL() + "." + signedJWT.getPayload().toBase64URL())
                    .getBytes(StandardCharsets.US_ASCII);
            verifier.update(signingInput);
            logger.debug("Verifier updated with signing input.");
            byte[] signatureBytes = Base64.getUrlDecoder().decode(signedJWT.getSignature().toString());
            logger.debug("Signature bytes decoded.");
            if (!verifier.verify(signatureBytes)) {
                logger.warn("JWT signature verification failed.");
                throw new JwtException("Invalid signature");
            } else {
                logger.info("JWT signature verification successful.");
                return createJWT(signedJWT);
            }


        } catch (NoSuchAlgorithmException | ParseException | InvalidAlgorithmParameterException | InvalidKeyException |
                 SignatureException | InvalidKeySpecException | NoSuchProviderException | IOException |
                 InterruptedException e) {
            logger.error("An error occurred during JWT decoding.", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private RSAPublicKey createRSAKey(SignedJWT signedJWT) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        logger.info("Creating RSA Public Key from JWT.");
        String keyId = signedJWT.getHeader().getKeyID();
        logger.debug("JWT Key ID (kid): {}", keyId);
        Map<String, List> jwks = getJwks();
        logger.debug("Retrieved JWKS: {}", jwks);
        if (jwks.isEmpty()) {
            logger.error("JWKS is empty.");
            throw new JwtException("JWKS cannot be null");
        }
        Map<String, String> sign = findSign(jwks.get("keys"), keyId);
        logger.debug("Signing key found in JWKS: {}", sign);
        if (sign.isEmpty()) {
            logger.error("Signing key with ID: {} not found in JWKS.", keyId);
            throw new JwtException("Keys cannot be null");
        }
        String modulus = sign.get("n");
        String exponent = sign.get("e");
        if (modulus == null || modulus.isEmpty() || exponent == null || exponent.isEmpty()) {
            logger.error("Modulus (n) or exponent (e) is null or empty in the signing key.");
            throw new JwtException("Modulus and exponent cannot be null");
        }
        RSAPublicKeySpec spec = new RSAPublicKeySpec(
                new BigInteger(1, Base64.getUrlDecoder().decode(modulus)),
                new BigInteger(1, Base64.getUrlDecoder().decode(exponent))
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        logger.debug("KeyFactory instance created for RSA.");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(spec);
        logger.info("RSA Public Key created successfully.");
        return publicKey;
    }

    private Jwt createJWT(SignedJWT signedJWT) throws ParseException {
        logger.info("Creating Spring Security JWT object.");
        Map<String, Object> signedJWTPayload = signedJWT.getPayload().toJSONObject();
        Instant iat = null;
        if (signedJWTPayload.containsKey("iat")) {
            iat = Instant.ofEpochMilli(((Number) signedJWTPayload.get("iat")).longValue() * 1000);
            logger.debug("Issued At (iat): {}", iat);
        }
        Instant exp = null;
        if (signedJWTPayload.containsKey("exp")) {
            exp = Instant.ofEpochMilli(((Number) signedJWTPayload.get("exp")).longValue() * 1000);
            logger.debug("Expiration Time (exp): {}", exp);
        }
        Jwt jwt = new Jwt(
                signedJWT.getParsedString(),
                iat,
                exp,
                signedJWT.getHeader().toJSONObject(),
                signedJWT.getJWTClaimsSet().toJSONObject()

        );
        logger.info("Spring Security JWT object created.");
        return jwt;

    }

    @SuppressWarnings("unchecked")
    private Map<String, List> getJwks() throws IOException, InterruptedException {
        logger.info("Fetching JWKS from URI: {}", jwksUri);
        try {
            HttpClient client = HttpClient.newHttpClient();
            logger.debug("HttpClient created.");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jwksUri))
                    .GET()
                    .build();
            logger.debug("HttpRequest built for URI: {}", jwksUri);

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            logger.debug("HttpResponse received with status code: {}", response.statusCode());

            if (response.statusCode() != 200) {
                logger.error("Failed to fetch JWKS. HTTP Status Code: {}", response.statusCode());
                throw new RuntimeException("Failed to fetch JWKS: HTTP " + response.statusCode());
            }
            Map<String, List> jwksMap = objectMapper.readValue(response.body(), Map.class);
            logger.debug("JWKS parsed successfully: {}", jwksMap);
            return jwksMap;

        } catch (IOException e) {
            logger.error("IOException occurred while fetching JWKS.", e);
            throw e;
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred while fetching JWKS.", e);
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching JWKS.", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> findSign(List<Map<String, String>> keys, String kid) {
        logger.info("Finding signing key with ID: {} in JWKS keys.", kid);
        if (keys == null) {
            logger.warn("JWKS keys list is null.");
            throw new NoSuchElementException("No key found");
        }
        for (Map<String, String> key : keys) {
            if (key.containsKey("kid") && key.get("kid").equals(kid)) {
                logger.info("Found signing key with ID: {}.", kid);
                logger.debug("Signing key details: {}", key);
                return key;
            }
        }
        logger.warn("No signing key found with ID: {}.", kid);
        throw new NoSuchElementException("No key found");
    }
}