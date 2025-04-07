package aj.FiTracker.FiTracker.DTO.REST;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseZoneRequest {
    @NotBlank
    private String zoneName;
    private String zoneDescription;
}
