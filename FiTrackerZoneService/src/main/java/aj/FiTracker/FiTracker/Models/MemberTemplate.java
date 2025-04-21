package aj.FiTracker.FiTracker.Models;


import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Interfaces.KafkaModelTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MemberTemplate implements KafkaModelTemplate {

    private List<Member> membersList;
    private String zoneId;

    public MemberTemplate(String zoneId) {
        this.zoneId = zoneId;
        this.membersList = new ArrayList<>();
    }

    public void addMember(Zone.Member member) {
        this.membersList.add(new Member(member.getUserId()));
    }

    public void addMembers(List<Zone.Member> members) {
        members.forEach(this::addMember);
    }

    public record Member(long memberId) {
    }

}
