package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BaseExpense {
    @NotBlank
    private String zoneId;
    @NotBlank
    private String currency;
    @NotNull
    private BigDecimal value;
    @NotBlank
    private String name;
    @NotEmpty
    private List<Long> categoriesIds;

    public BaseExpense() {

    }

    public BaseExpense(Expense expense) {
        this.currency = expense.getCurrency();
        this.name = expense.getName();
        this.value = expense.getValue();
        this.categoriesIds = expense.getCategories().stream().map(Category::getCategoryId).toList();
    }
}
