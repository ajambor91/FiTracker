package aj.FiTracker.FiTracker.Controllers;
import aj.FiTracker.FiTracker.DTO.REST.NewZone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zones")
public class ZoneController {

    @PostMapping("/zone")
    public ResponseEntity<?> createZone() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}