package aj.FiTracker.FiTracker.Factories;

import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Models.MemberTemplate;

public class MembersFactory {

    public static MemberTemplate createMemberTemplate(Zone zone) {
        MemberTemplate memberTemplate = new MemberTemplate(zone.getId());
        zone.getMembers().forEach(memberTemplate::addMember);
        return memberTemplate;
    }
}
