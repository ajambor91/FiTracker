package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeletedZoneResponse extends BaseZoneResponse {
    @NotBlank
    private LocalDateTime deletedAt;

    public DeletedZoneResponse(Zone zone) {
        super(zone);
        deletedAt = zone.getDeletedAt();
    }
}
