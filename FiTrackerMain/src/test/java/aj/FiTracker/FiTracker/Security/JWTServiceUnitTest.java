package aj.FiTracker.FiTracker.Security;


import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

import static aj.FiTracker.FiTracker.TestUtils.TestData.TEST_USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("unit")
public class JWTServiceUnitTest {
    private SecretKey jwt;
    private String secretKeyString = "U1VQRVJfU0VDUkVUX0tFWV9TVVBFUl9TRUNSRVRfS0VZ";
    private long tokenExpiration = 3600000;
    private JWTService jwtService;
    private User testUser;
    private JwtParser parser;
    @BeforeEach
    public void setup() {
        this.testUser = UserDataTestFactory.createTestUser();
        this.jwtService= new JWTService(secretKeyString, tokenExpiration);
        this.jwt = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parser().verifyWith(this.jwt).build();
    }

    @Test
    @DisplayName("Should generate token with default expiration")
    public void testGenerateUser() {
        User userWithJWT = jwtService.generateToken(this.testUser);
        Jws<Claims> claimsJwt = parser.parseSignedClaims(userWithJWT.getJwt());
        Claims payload = claimsJwt.getPayload();
        assertNotNull(userWithJWT.getJwt());
        assertEquals(TEST_USER_NAME, payload.getSubject());
    }

    @Test
    @DisplayName("Should generate token with custom expiration")
    public void testGenerateUserWithCustomExpiration() {
        User userWithJWT = jwtService.generateToken(this.testUser, tokenExpiration);
        Jws<Claims> claimsJwt = parser.parseSignedClaims(userWithJWT.getJwt());
        Claims payload = claimsJwt.getPayload();
        assertNotNull(userWithJWT.getJwt());
        assertEquals(TEST_USER_NAME, payload.getSubject());
    }
}
