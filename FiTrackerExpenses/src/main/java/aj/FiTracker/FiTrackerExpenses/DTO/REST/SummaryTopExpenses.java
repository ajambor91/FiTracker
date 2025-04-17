package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryTopExpenses {
    private List<TopExpense> expenses;

    public SummaryTopExpenses(List<TopExpense> expenses) {
        this.expenses = expenses;
    }
}
