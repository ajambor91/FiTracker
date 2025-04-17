package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GetCategoriesResponse {
    List<CategoryDb> categories;

    public GetCategoriesResponse(List<CategoryDb> categories) {
        this.categories = categories;
    }
}
