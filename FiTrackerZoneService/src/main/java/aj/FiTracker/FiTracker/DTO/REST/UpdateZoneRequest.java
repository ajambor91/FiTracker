package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateZoneRequest extends BaseZoneRequest{
    @NotBlank
    private String zoneId;
    private List<Zone.Member> members;

    public UpdateZoneRequest(){}
}
