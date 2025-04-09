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

    public record Member(long memberId){}
    private List<Member> memberList;
    private String zoneId;
    public MemberTemplate(String zoneId) {
        this.zoneId = zoneId;
        this.memberList = new ArrayList<>();
    }
    public void addMember(Zone.Member member) {
        this.memberList.add(new Member(member.getUserId()));
    }

}
