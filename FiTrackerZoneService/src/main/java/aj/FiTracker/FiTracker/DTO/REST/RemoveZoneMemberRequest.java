package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Documents.Zone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RemoveZoneMemberRequest {
    @NotBlank
    private String zoneId;
    @NotEmpty
    private List<Zone.Member> membersList;

    public RemoveZoneMemberRequest() {
    }
}
