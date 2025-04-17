package aj.FiTracker.FiTrackerExpenses.DTO.DB;

import java.math.BigDecimal;
import java.sql.Date;

public record TotalSummaryByDate(Date date, BigDecimal expensesValue) {
}
