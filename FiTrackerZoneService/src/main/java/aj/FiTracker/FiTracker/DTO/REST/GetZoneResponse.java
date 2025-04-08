package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetZoneResponse extends BaseZoneResponse {

    public GetZoneResponse(Zone zone) {
        super(zone);
    }
}
