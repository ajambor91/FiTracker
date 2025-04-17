package aj.FiTracker.FiTrackerExpenses.DTO.REST;


import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryByCategory {
    private List<TotalSummaryByCategory> summaries;

    public SummaryByCategory(List<TotalSummaryByCategory> summaries) {
        this.summaries = summaries;
    }
}
