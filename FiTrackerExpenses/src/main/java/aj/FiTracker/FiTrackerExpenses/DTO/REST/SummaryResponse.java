package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SummaryResponse {


    private String zoneId;
    private String groupBy;
    private String periodType;
    private String period;
    private long userId;
    private List<Long> categoriesIds;
    private String currency;
    private BigDecimal total;
    private List<SummaryExpense> expenses;

    public record SummaryExpense(long userId, List<Long> categoriesIds, String currency) {
    }
}
