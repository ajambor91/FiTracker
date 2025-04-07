package aj.FiTracker.FiTracker.DTO.REST;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetZoneRequest {
    @NotBlank
    private long id;
    public GetZoneRequest() {

    }
}
