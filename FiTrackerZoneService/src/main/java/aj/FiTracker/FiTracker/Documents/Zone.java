package aj.FiTracker.FiTracker.Documents;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "zone")
public class Zone {
    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private long ownerId;

    private String description;
    private List<Member> membersList;

    @NotNull
    private LocalDateTime createdAt;
    @NotBlank
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Zone() {
    }

    public Zone(NewZoneRequest newZoneRequest, long ownerId) {
        this.membersList = new ArrayList<>();
        this.ownerId = ownerId;
        this.name = newZoneRequest.getZoneName();
        this.description = newZoneRequest.getZoneDescription();
    }

    public void addMember(Member member) {
        this.membersList.add(member);
    }

    @Getter
    @Setter
    public static class Member {
        private long userId;
        private MemberRole role;
        private String name;
        private LocalDateTime addedAt;

        public Member(long userId, MemberRole role, String name) {
            this.userId = userId;
            this.role = role;
            this.name = name;
            this.addedAt = LocalDateTime.now();
        }
    }


}
