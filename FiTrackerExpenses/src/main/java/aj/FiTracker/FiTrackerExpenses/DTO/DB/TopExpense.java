package aj.FiTracker.FiTrackerExpenses.DTO.DB;

import java.math.BigDecimal;
import java.sql.Date;

public record TopExpense(String expenseName, BigDecimal expenseValue, String categoryName, Date date) {
}
