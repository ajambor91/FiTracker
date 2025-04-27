package aj.FiTracker.FiTrackerExpenses.Models;


import aj.FiTracker.FiTrackerExpenses.Interfaces.KafkaModelTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MembersTemplate implements KafkaModelTemplate {

    private List<Member> membersList;
    private String zoneId;

    public MembersTemplate() {
    }

    public MembersTemplate(String zoneId) {
        this.zoneId = zoneId;
        this.membersList = new ArrayList<>();
    }

    public record Member(long memberId) {
    }


}
