package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import aj.FiTracker.FiTracker.TestUtils.VaultDataFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.Signature;
import org.springframework.vault.support.VaultTransitKey;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("unit")
public class JWTServiceUnitTest {
    private SecretKey jwt;
    private final long tokenExpiration = 3600000;
    private JWTService jwtService;
    private User testUser;
    private JwtParser parser;
    private VaultTransitOperations vaultTransitOperations;
    private ObjectMapper objectMapper;
    private KeyPair pair;
    private Signature signatureMock;
    private VaultTransitKey vaultTransitKey;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        vaultTransitKey = VaultDataFactory.createTestVaultTransitKey();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        pair = keyGen.generateKeyPair();
        this.objectMapper = new ObjectMapper();
        this.vaultTransitOperations = mock(VaultTransitOperations.class);
        when(this.vaultTransitOperations.getKey(any(String.class))).thenReturn(vaultTransitKey);
        this.signatureMock = mock(Signature.class);
        when(signatureMock.getSignature()).thenReturn("SIGNED");
        when(this.vaultTransitOperations.sign(eq("jwt-rsa-key"), any(Plaintext.class))).thenReturn(signatureMock);
        this.testUser = UserDataTestFactory.createTestUser();
        this.parser = Jwts.parser().verifyWith(this.pair.getPublic()).build();
        this.jwtService = new JWTService(vaultTransitOperations, objectMapper, tokenExpiration);
    }

    @Test
    @DisplayName("Should generate token with default expiration")
    public void testGenerateUser() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        User userWithJWT = jwtService.generateToken(this.testUser);
        assertNotNull(userWithJWT.getJwt());
    }

    @Test
    @DisplayName("Should generate token with custom expiration")
    public void testGenerateUserWithCustomExpiration() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        User userWithJWT = jwtService.generateToken(this.testUser, tokenExpiration);
        assertNotNull(userWithJWT.getJwt());
    }
}
