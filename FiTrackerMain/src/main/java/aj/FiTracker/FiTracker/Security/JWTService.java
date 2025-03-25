package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTService {
    private final Logger logger;
    private final long tokenExpiration;
    private final SecretKey secretKey;
    @Autowired
    public JWTService(   @Value("${login.jwt.secret}") String secretKey,
                         @Value("${login.expiration}") Long tokenExpiration) {
        this.logger = LoggerFactory.getLogger(JWTService.class);
        this.tokenExpiration = tokenExpiration;
        this.secretKey =  Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));;
    }

    public User generateToken(User user) {
        return this.generateToken(user, tokenExpiration);
    }

    public User generateToken(User user, long expirationTime) {
        String token = Jwts.builder()
                .claims()
                .subject(user.getName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .and()
                .signWith(secretKey)
                .compact();
        user.setJwt(token);
        return user;
    }
}
