package aj.FiTracker.FiTracker.Security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.util.UUID;

public class CsrfCookieTokenRepository implements CsrfTokenRepository {
    private static final Logger lopgger = LoggerFactory.getLogger(CsrfToken.class);
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        lopgger.info("Generating a new CSRF token");
        String token = UUID.randomUUID().toString();
        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_COOKIE_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        lopgger.info("Saving CSRF toke.");
        if (token == null) {
            lopgger.info("CSRF token is null, deleting CSRF cookie");
            Cookie cookie = new Cookie(CSRF_COOKIE_NAME, "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return;
        }
        Cookie cookie = new Cookie(CSRF_COOKIE_NAME, token.getToken());
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        lopgger.info("Loading CSRF token from cookie");

        String token = getCookieValue(request, CSRF_COOKIE_NAME);
        if (token == null) {
            lopgger.info("CSRF cookie not found");

            return null;
        }
        lopgger.debug("Loaded CSRF token from cookie");

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_COOKIE_NAME, token);
    }


    private String getCookieValue(HttpServletRequest request, String cookieName) {
        lopgger.info("Getting value of cookie with name: {}", cookieName);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        lopgger.info("Cookie with name '{}' not found", cookieName);
        return null;
    }
}
