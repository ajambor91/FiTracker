package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Services.ExpensesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    private final Logger logger = LoggerFactory.getLogger(ExpensesController.class);

    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        logger.info("Initializing ExpensesController.");
        this.expensesService = expensesService;
    }

    @PostMapping("/zone/{zoneId}/expense")
    public ResponseEntity<AddExpenseResponse> addExpense(Authentication authentication, @PathVariable String zoneId, @Valid @RequestBody AddExpenseRequest addExpenseRequest) {
        logger.info("Received request to add an expense to zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Add expense request details: {}", addExpenseRequest);
        Expense expense = this.expensesService.addExpense(addExpenseRequest, zoneId, authentication);
        logger.info("Successfully added expense with ID: {} in zone {}.", expense.getExpenseId(), zoneId);
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
        logger.info("Received request to get summary for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, categoriesIds={}, groupBy={}, periodStart={}, periodEnd={}, userId={}",
                currency, categoriesIds, groupBy, periodStart, periodEnd, userId);

        if (groupBy == null || groupBy.isEmpty() || groupBy.equals("category")) {
            logger.debug("Grouping by category. Calling ExpensesService.getSummaryByCategory.");
            List<TotalSummaryByCategory> data = this.expensesService.getSummaryByCategory(zoneId, currency, periodStart, periodEnd, authentication);
            logger.info("Successfully retrieved summary by category for zone {}. Found {} entries.", zoneId, data.size());
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryByCategory(data));
        } else if (groupBy.equals("day")) {
            logger.debug("Grouping by day. Calling ExpensesService.getSummaryByDate.");
            List<TotalSummaryByDate> data = this.expensesService.getSummaryByDate(zoneId, currency, periodStart, periodEnd, authentication);
            logger.info("Successfully retrieved summary by day for zone {}. Found {} entries.", zoneId, data.size());
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryByDate(data));
        }

        logger.warn("Invalid 'groupBy' parameter received: {}. Returning 404.", groupBy);
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
        logger.info("Received request to get top summary for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, categoriesIds={}, type={}, periodStart={}, periodEnd={}, userId={}",
                currency, categoriesIds, type, periodStart, periodEnd, userId);

        if (type == null || type.isEmpty() || type.equals("expenses")) {
            logger.debug("Summary type is expenses. Calling ExpensesService.getSummaryTopExpenses.");
            List<TopExpense> data = this.expensesService.getSummaryTopExpenses(zoneId, currency, periodStart, periodEnd, authentication);
            logger.info("Successfully retrieved top expenses summary for zone {}. Found {} entries.", zoneId, data.size());
            return ResponseEntity.status(HttpStatus.OK).body(new SummaryTopExpenses(data));
        }

        logger.warn("Invalid 'type' parameter received for top summary: {}. Returning 404.", type);
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
        logger.info("Received request to get total sum summary for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, categoriesIds={}, groupBy={}, periodStart={}, periodEnd={}, userId={}",
                currency, categoriesIds, groupBy, periodStart, periodEnd, userId);

        if (groupBy == null || groupBy.isEmpty() || groupBy.equals("category")) {
            logger.debug("Calling ExpensesService.getSummarySum.");
            List<ExpensesSum> data = this.expensesService.getSummarySum(zoneId, currency, periodStart, periodEnd, authentication);
            logger.info("Successfully retrieved total sum summary for zone {}. Found {} entries.", zoneId, data.size());
            return ResponseEntity.status(HttpStatus.OK).body(new GeneralSum(data));
        }

        logger.warn("Invalid 'groupBy' parameter received for sum summary: {}. Returning 404. Note: Current sum summary logic may not fully utilize groupBy.", groupBy);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}