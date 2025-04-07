package aj.FiTracker.FiTrackerExpenses.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import java.util.*;

public class JWTDecode implements JwtDecoder {
    private static JWTDecode jwtDecode;


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
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private JWTDecode() {
        this.objectMapper = new ObjectMapper();
    }

    public static JWTDecode getDecoder() {
        if (jwtDecode == null) {
            JWTDecode.jwtDecode = new JWTDecode();

        }
        return JWTDecode.jwtDecode;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            RSAPublicKey rsaPublicKey = createRSAKey(signedJWT);
            Signature verifier = Signature.getInstance(SECURITY_ALGORITHM, PROVIDER);
            verifier.initVerify(rsaPublicKey);
            verifier.setParameter(new PSSParameterSpec(
                    MD_NAME,
                    MGF_NAME,
                    MGF1ParameterSpec.SHA256,
                    SALT_LEN,
                    TRAILER
            ));
            byte[] signingInput = (signedJWT.getHeader().toBase64URL() + "." + signedJWT.getPayload().toBase64URL())
                    .getBytes(StandardCharsets.US_ASCII);
            verifier.update(signingInput);
            byte[] signatureBytes = Base64.getUrlDecoder().decode(signedJWT.getSignature().toString());
            if (!verifier.verify(signatureBytes)) {
                throw new JwtException("Invalid signature");
            } else {
                return createJWT(signedJWT);
            }


        } catch (NoSuchAlgorithmException | ParseException | InvalidAlgorithmParameterException | InvalidKeyException |
                 SignatureException | InvalidKeySpecException | NoSuchProviderException | IOException |
                 InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private RSAPublicKey createRSAKey(SignedJWT signedJWT) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        Map<String, List> jwks = getJwks();
        if (jwks.isEmpty()) {
            throw new JwtException("JWKS cannot be null");
        }
        Map<String, String> sign = findSign(jwks.get("keys"), signedJWT.getHeader().getKeyID());
        if (sign.isEmpty()) {
            throw new JwtException("Keys cannot be null");
        }
        if (sign.get("n").isEmpty() || sign.get("e").isEmpty()) {
            throw new JwtException("Modulus and exponant cannot be null");
        }
        RSAPublicKeySpec spec = new RSAPublicKeySpec(
                new BigInteger(1, Base64.getUrlDecoder().decode(sign.get("n"))),
                new BigInteger(1, Base64.getUrlDecoder().decode(sign.get("e")))
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    private Jwt createJWT(SignedJWT signedJWT) throws ParseException {
        Map<String, Object> signedJWTPayload = signedJWT.getPayload().toJSONObject();
        Instant iat = Instant.ofEpochMilli((Long) signedJWTPayload.get("iat"));
        Instant exp = Instant.ofEpochMilli((Long) signedJWTPayload.get("exp"));
        return new Jwt(
                signedJWT.getParsedString(),
                iat,
                exp,
                signedJWT.getHeader().toJSONObject(),
                signedJWT.getJWTClaimsSet().toJSONObject()

        );

    }

    @SuppressWarnings("unchecked")
    private Map<String, List> getJwks() throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jwksUri))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch JWKS: HTTP " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), Map.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> findSign(List<Map<String, String>> keys, String kid) {

        for (Map<String, String> key : keys) {
            if (key.get("kid").equals(kid)) {
                return key;
            }
        }
        throw new NoSuchElementException("No key found");
    }
}
