package aj.FiTracker.FiTracker.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
public class CoreController {
    @GetMapping("/csrf-token")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken) {
        csrfToken.getToken();

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
