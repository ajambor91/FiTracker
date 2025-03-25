package aj.FiTracker.FiTracker.Security;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("unit")
public class CsrfCookieTokenRepositoryUnitTest {
    private String CSRF_COOKIE_NAME;
    private String CSRF_HEADER_NAME;
    private CsrfCookieTokenRepository csrfCookieTokenRepository;
    private HttpServletRequest httpServletRequestMock;
    private HttpServletResponse httpServletResponseMock;
    private CsrfToken csrfTokenMock;
    private Cookie testCookie;
    private String testCookieValue;
    @BeforeEach
    public void setup() {

        CSRF_COOKIE_NAME  = "XSRF-TOKEN";
        CSRF_HEADER_NAME = "X-XSRF-TOKEN";
        testCookieValue = "TestCookieValue";
        this.testCookie = new Cookie(CSRF_COOKIE_NAME, testCookieValue);
        this.testCookie.setPath("/");
        this.testCookie.setHttpOnly(false);
        this.testCookie.setSecure(true);
        this.testCookie.setAttribute("SameSite", "Strict");
        csrfCookieTokenRepository = new CsrfCookieTokenRepository();
        httpServletRequestMock = mock(HttpServletRequest.class);
        csrfTokenMock = mock(CsrfToken.class);
        httpServletResponseMock = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Should save token")
    public void testSaveToken() {
        String cookieValue = "CookieValue";
        when(csrfTokenMock.getToken()).thenReturn(testCookieValue);
        csrfCookieTokenRepository.saveToken(csrfTokenMock, httpServletRequestMock, httpServletResponseMock);
        verify(httpServletResponseMock).addCookie(eq(testCookie));
    }

    @Test
    @DisplayName("Should save token when token is null")
    public void testSaveTokenWhenTokenNull() {
        Cookie testClearedCookie = new Cookie(CSRF_COOKIE_NAME, "");
        testClearedCookie.setPath("/");
        testClearedCookie.setMaxAge(0);

        csrfCookieTokenRepository.saveToken(null, httpServletRequestMock, httpServletResponseMock);
        verify(httpServletResponseMock).addCookie(eq(testClearedCookie));
    }

    @Test
    @DisplayName("Should load token from cookie")
    public void testLoadToken() {
        String cookieTestValue = "TestCookieValue";
        Cookie[] cookies = {this.testCookie};
        when(httpServletRequestMock.getCookies()).thenReturn(cookies);
        CsrfToken csrfToken = csrfCookieTokenRepository.loadToken(httpServletRequestMock);
        assertEquals("X-XSRF-TOKEN", csrfToken.getHeaderName());
        assertEquals("XSRF-TOKEN", csrfToken.getParameterName());
        assertEquals("TestCookieValue", csrfToken.getToken());
    }

    @Test
    @DisplayName("Should return null when cannot find token in cookies")
    public void testLoadTokenAndReturnsNullWhenCannotFindCookies() {

        when(httpServletRequestMock.getCookies()).thenReturn(null);
        CsrfToken csrfToken = csrfCookieTokenRepository.loadToken(httpServletRequestMock);
        assertNull(csrfToken);
    }

    @Test
    @DisplayName("Should return new random token generateToken")
    public void testGenerateToken() {

        CsrfToken csrfToken = csrfCookieTokenRepository.generateToken(httpServletRequestMock);
        assertEquals("X-XSRF-TOKEN", csrfToken.getHeaderName());
        assertEquals("XSRF-TOKEN", csrfToken.getParameterName());
        assertNotNull(csrfToken.getToken());
        assertInstanceOf(String.class, csrfToken.getToken());
    }

}
