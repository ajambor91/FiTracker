package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ZonesResponse {
    private List<ZoneResponse> zones;
    private static class ZoneResponse extends BaseZoneResponse {

        public ZoneResponse(Zone zone) {
            super(zone);
        }
    }

    public ZonesResponse(List<Zone> resZones) {
        zones = new ArrayList<>();
        resZones.forEach(zone -> {
            zones.add(new ZoneResponse(zone));
        });
    }
}
