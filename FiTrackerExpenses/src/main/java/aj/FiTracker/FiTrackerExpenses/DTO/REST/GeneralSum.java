package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeneralSum {
    private List<ExpensesSum> sum;

    public GeneralSum(List<ExpensesSum> expensesSums) {
        this.sum = expensesSums;
    }
}
