package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SummaryByDate {
    private List<TotalSummaryByDate> summaries;

    public SummaryByDate(List<TotalSummaryByDate> summaries) {
        this.summaries = summaries;
    }
}
