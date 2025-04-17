package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public abstract class BaseZoneResponse {
    private String zoneId;
    private String zoneName;
    private String zoneDescription;
    private LocalDateTime createdAt;
    private long ownerId;
    private List<Zone.Member> membersList;

    public BaseZoneResponse(Zone zone) {
        this.zoneDescription = zone.getDescription();
        this.zoneId = zone.getId();
        this.createdAt = zone.getCreatedAt();
        this.ownerId = zone.getOwnerId();
        this.zoneName = zone.getName();
        this.membersList = zone.getMembersList();
    }
}
