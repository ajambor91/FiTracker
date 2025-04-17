package aj.FiTracker.FiTrackerExpenses.DTO.REST;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddCategoryRequest extends BaseCategory {

    @NotBlank
    private String zoneId;
}
