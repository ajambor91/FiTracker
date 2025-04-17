package aj.FiTracker.FiTrackerExpenses.Security;

import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public final class JWTClaimsUtil {
    public static JWTClaims getUsernameFromClaims(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwtClaims) {
            return new JWTClaims(
                    jwtClaims.getClaimAsString("name"),
                    parseStringToLong(jwtClaims.getClaimAsString("sub")));
        }
        throw new InternalServerException("Cannot find any claims");
    }

    private static long parseStringToLong(String data) {
        return Long.parseLong(data);
    }

    public record JWTClaims(String name, long userId) {
    }
}
