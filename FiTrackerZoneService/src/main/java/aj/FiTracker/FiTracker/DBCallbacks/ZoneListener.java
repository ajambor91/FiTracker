package aj.FiTracker.FiTracker.DBCallbacks;

import aj.FiTracker.FiTracker.Documents.Zone;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ZoneListener extends AbstractMongoEventListener<Zone> {


    @Override
    public void onBeforeConvert(BeforeConvertEvent<Zone> event) {
        Zone zone = event.getSource();

        if (zone.getId() == null || zone.getCreatedAt() == null) {
            zone.setCreatedAt(LocalDateTime.now());
        }
        zone.setUpdatedAt(LocalDateTime.now());
    }
}