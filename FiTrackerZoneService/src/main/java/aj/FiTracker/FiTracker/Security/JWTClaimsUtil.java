package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public final class JWTClaimsUtil {
    public record JWTClaims(String name, long userId){}
    public static JWTClaims getUsernameFromClaims(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwtClaims = (Jwt) authentication.getPrincipal();
            return new JWTClaims(
                    jwtClaims.getClaimAsString("name"),
                    parseStringToLong(jwtClaims.getClaimAsString("sub")));
        }
        throw new InternalServerException("Cannot find any claims");
    }

    private static long parseStringToLong(String data) {
        return Long.parseLong(data);
    }
}
