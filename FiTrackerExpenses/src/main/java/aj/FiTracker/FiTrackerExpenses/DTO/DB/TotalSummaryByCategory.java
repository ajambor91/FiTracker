package aj.FiTracker.FiTrackerExpenses.DTO.DB;

import java.math.BigDecimal;

public record TotalSummaryByCategory(String categoryName, BigDecimal expensesValue) {
}
