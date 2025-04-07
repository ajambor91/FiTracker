package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewZoneResponse extends BaseZoneResponse{

    public NewZoneResponse(Zone zone) {
        super(zone);
    }
}
