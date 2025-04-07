package aj.FiTracker.FiTrackerExpenses.Controllers;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    @PostMapping("/expense")
    public ResponseEntity<?> createZone() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}