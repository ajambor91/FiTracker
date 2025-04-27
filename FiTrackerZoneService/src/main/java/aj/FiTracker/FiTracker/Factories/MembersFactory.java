package aj.FiTracker.FiTracker.Factories;

import aj.FiTracker.FiTracker.DTO.REST.RemoveZoneMemberRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Models.MembersTemplate;

public class MembersFactory {

    public static MembersTemplate createMemberTemplate(Zone zone) {
        MembersTemplate membersTemplate = new MembersTemplate(zone.getId());
        zone.getMembersList().forEach(membersTemplate::addMember);
        return membersTemplate;
    }

    public static MembersTemplate createMemberTemplate(UpdateZoneRequest zone) {
        MembersTemplate membersTemplate = new MembersTemplate(zone.getZoneId());
        zone.getMembersList().forEach(membersTemplate::addMember);
        return membersTemplate;
    }

    public static MembersTemplate createMemberTemplate(RemoveZoneMemberRequest zone) {
        MembersTemplate membersTemplate = new MembersTemplate(zone.getZoneId());
        zone.getMembersList().forEach(membersTemplate::addMember);
        return membersTemplate;
    }
}
