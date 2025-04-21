package aj.FiTracker.FiTrackerExpenses.DTO.DB;


import java.math.BigDecimal;

public record ExpensesSum(String categoryName, BigDecimal categoryValue, BigDecimal overallSum) {
}

