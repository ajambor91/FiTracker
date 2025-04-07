package aj.FiTracker.FiTracker.DBCallbacks;

import aj.FiTracker.FiTracker.Documents.Zone;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ZoneCallback implements BeforeSaveCallback<Zone> {
    @Override
    public Zone onBeforeSave(Zone entity, Document document, String collection) {
        if (entity.getId() == null || entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }


}
