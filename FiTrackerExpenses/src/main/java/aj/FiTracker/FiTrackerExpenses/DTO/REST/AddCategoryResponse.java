package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCategoryResponse extends BaseCategory {
    @NotBlank
    private String zoneId;
    private long categoryId;
    public AddCategoryResponse(Category category) {

        super(category);
        this.zoneId = category.getZoneId();
        this.categoryId = category.getCategoryId();

    }
}
