package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;

import java.util.ArrayList;
import java.util.List;

public final class ZoneFactory {

    public static String ZONE_TEST_ID = "67f3b66a59153d2661e64002";
    public static String ZONE_TEST_NAME = "New zone";
    public static String ZONE_TEST_DESCRIPTION = "Test decscription";
    public static String ZONE_TEST_NAME_SECOND = "New second zone";
    public static String ZONE_TEST_DESCRIPTION_SECOND = "Test second decscription";

    public static String USER_TEST_NAME = "Test user";
    public static String MEMBER_TEST_NAME = "Test member";
    public static long OWNER_TEST_ID = 1;
    public static long MEMBER_TEST_ID = 2;

    public static NewZoneRequest createNewZoneTestRequest() {
        NewZoneRequest zone = new NewZoneRequest();
        zone.setZoneName(ZONE_TEST_NAME);
        zone.setZoneDescription(ZONE_TEST_DESCRIPTION);
        return zone;
    }

    public static UpdateZoneRequest createUpdateZoneTestRequest() {
        UpdateZoneRequest zone = new UpdateZoneRequest();
        zone.setZoneName(ZONE_TEST_NAME);
        zone.setZoneDescription(ZONE_TEST_DESCRIPTION);
        return zone;
    }
    public static UpdateZoneRequest createUpdateZoneTestRequestWithMember() {
        UpdateZoneRequest zone = new UpdateZoneRequest();
        zone.setZoneName(ZONE_TEST_NAME);
        zone.setZoneDescription(ZONE_TEST_DESCRIPTION);
        List<Zone.Member> list = new ArrayList<>();
        list.add(createSecondMember());
        zone.setMembers(list);
        return zone;
    }

    public static UpdateZoneRequest createUpdateZoneTestRequestWithNameAndDescription() {
        UpdateZoneRequest zone = new UpdateZoneRequest();
        zone.setZoneName(ZONE_TEST_NAME_SECOND);
        zone.setZoneDescription(ZONE_TEST_DESCRIPTION_SECOND);
        return zone;
    }



    public static Zone.Member createSecondMember() {
        return new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME);
    }



}
