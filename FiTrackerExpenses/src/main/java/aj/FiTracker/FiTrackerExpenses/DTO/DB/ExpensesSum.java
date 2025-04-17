package aj.FiTracker.FiTrackerExpenses.DTO.DB;


import java.math.BigDecimal;
import java.util.List;

public record ExpensesSum(String categoryName, BigDecimal categoryValue, BigDecimal overallSum) {
}

