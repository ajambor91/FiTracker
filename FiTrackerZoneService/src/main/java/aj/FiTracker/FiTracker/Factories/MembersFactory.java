package aj.FiTracker.FiTracker.Factories;

import aj.FiTracker.FiTracker.DTO.REST.RemoveZoneMemberRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Models.MemberTemplate;

public class MembersFactory {

    public static MemberTemplate createMemberTemplate(Zone zone) {
        MemberTemplate memberTemplate = new MemberTemplate(zone.getId());
        zone.getMembersList().forEach(memberTemplate::addMember);
        return memberTemplate;
    }

    public static MemberTemplate createMemberTemplate(UpdateZoneRequest zone) {
        MemberTemplate memberTemplate = new MemberTemplate(zone.getZoneId());
        zone.getMembersList().forEach(memberTemplate::addMember);
        return memberTemplate;
    }

    public static MemberTemplate createMemberTemplate(RemoveZoneMemberRequest zone) {
        MemberTemplate memberTemplate = new MemberTemplate(zone.getZoneId());
        zone.getMembersList().forEach(memberTemplate::addMember);
        return memberTemplate;
    }
}
