package aj.FiTracker.FiTracker.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class CoreControllerUniTest {

    private CoreController corContoller;

    @BeforeEach
    public void setup() {
        this.corContoller = new CoreController();
    }

    @Test
    @DisplayName("Should return CSRF Token")
    public void testGetCsrfToken() {
        CsrfToken token = mock(CsrfToken.class);
        ResponseEntity<?> response =  this.corContoller.getCsrfToken(token);
        assertInstanceOf(ResponseEntity.class, response);
    }
}
