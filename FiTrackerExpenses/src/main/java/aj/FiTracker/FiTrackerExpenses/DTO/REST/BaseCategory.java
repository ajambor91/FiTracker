package aj.FiTracker.FiTrackerExpenses.DTO.REST;


import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseCategory {
    @NotBlank
    private String name;
    private String description;

    public BaseCategory() {
    }

    public BaseCategory(Category category) {
        this.description = category.getDescription();
        this.name = category.getName();
    }
}
