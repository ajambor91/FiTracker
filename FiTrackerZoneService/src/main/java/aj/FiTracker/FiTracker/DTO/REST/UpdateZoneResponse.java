package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateZoneResponse extends BaseZoneResponse {
    public UpdateZoneResponse(Zone zone) {
        super(zone);
    }
}
