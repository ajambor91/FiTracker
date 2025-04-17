package aj.FiTracker.FiTrackerExpenses.DTO.REST;


import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryResponse extends BaseCategory {
    private String zoneId;

    public UpdateCategoryResponse(Category category) {
        super(category);
        this.zoneId = category.getZoneId();
    }
}
