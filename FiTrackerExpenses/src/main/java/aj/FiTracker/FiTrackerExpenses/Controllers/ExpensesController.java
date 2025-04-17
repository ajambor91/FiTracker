package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Services.ExpensesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @PostMapping("/zone/{zoneId}/expense")
    public ResponseEntity<AddExpenseResponse> addExpense(Authentication authentication, @PathVariable String zoneId, @Valid @RequestBody AddExpenseRequest addExpenseRequest) {
        Expense expense = this.expensesService.addExpense(addExpenseRequest, zoneId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddExpenseResponse(expense));
    }

    @GetMapping("/zone/{zoneId}/summary")
    public ResponseEntity<?> getSummary(
            @PathVariable("zoneId") String zoneId,
            @RequestParam(name = "currency", required = false) String currency,
            @RequestParam(name = "categoriesIds", required = false) List<Long> categoriesIds,
            @RequestParam(name = "groupBy", required = false) String groupBy,
            @RequestParam(name = "periodStart", required = false) String periodStart,
            @RequestParam(name = "periodEnd", required = false) String periodEnd,
            @RequestParam(name = "userId", required = false) Long userId,
            Authentication authentication) {
        if (groupBy == null || groupBy.isEmpty() || groupBy.equals("category")) {
            List<TotalSummaryByCategory> data = this.expensesService.getSummaryByCategory(zoneId, currency, periodStart, periodEnd, authentication);
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryByCategory(data));
        } else if (groupBy.equals("day")) {
            List<TotalSummaryByDate> data = this.expensesService.getSummaryByDate(zoneId, currency, periodStart, periodEnd, authentication);
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryByDate(data));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @GetMapping("/zone/{zoneId}/summary/top")
    public ResponseEntity<?> getTopSummary(
            @PathVariable("zoneId") String zoneId,
            @RequestParam(name = "currency", required = false) String currency,
            @RequestParam(name = "categoriesIds", required = false) List<Long> categoriesIds,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "periodStart", required = false) String periodStart,
            @RequestParam(name = "periodEnd", required = false) String periodEnd,
            @RequestParam(name = "userId", required = false) Long userId,
            Authentication authentication) {
        if (type == null || type.isEmpty() || type.equals("expenses")) {
            List<TopExpense> data = this.expensesService.getSummaryTopExpenses(zoneId, currency, periodStart, periodEnd, authentication);
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryTopExpenses(data));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @GetMapping("/zone/{zoneId}/sum")
    public ResponseEntity<?> getSummarySum(
            @PathVariable("zoneId") String zoneId,
            @RequestParam(name = "currency", required = false) String currency,
            @RequestParam(name = "categoriesIds", required = false) List<Long> categoriesIds,
            @RequestParam(name = "groupBy", required = false) String groupBy,
            @RequestParam(name = "periodStart", required = false) String periodStart,
            @RequestParam(name = "periodEnd", required = false) String periodEnd,
            @RequestParam(name = "userId", required = false) Long userId,
            Authentication authentication) {
        if (groupBy == null || groupBy.isEmpty() || groupBy.equals("category")) {
            List<ExpensesSum> data = this.expensesService.getSummarySum(zoneId, currency, periodStart, periodEnd, authentication);
            return ResponseEntity.status(HttpStatus.OK).body(new GeneralSum(data));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }


}