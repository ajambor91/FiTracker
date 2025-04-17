package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddExpenseResponse extends BaseExpense {
    private long expenseId;

    public AddExpenseResponse(Expense expense) {
        super(expense);
        this.expenseId = expense.getExpenseId();
    }
}
