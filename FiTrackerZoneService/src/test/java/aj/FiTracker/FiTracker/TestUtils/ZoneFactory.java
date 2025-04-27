package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Models.MemberTemplate;

import java.time.LocalDateTime;
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
        zone.setMembersList(list);
        return zone;
    }

    public static UpdateZoneRequest createUpdateZoneTestRequestWithNameAndDescription() {
        UpdateZoneRequest zone = new UpdateZoneRequest();
        zone.setZoneName(ZONE_TEST_NAME_SECOND);
        zone.setZoneDescription(ZONE_TEST_DESCRIPTION_SECOND);
        return zone;
    }


    public static RemoveZoneMemberRequest createRemoveZoneMemberRequest() {
        RemoveZoneMemberRequest zone = new RemoveZoneMemberRequest();
        zone.setZoneId(ZONE_TEST_ID);
        zone.setMembersList(new ArrayList<>(List.of(new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME))));
        return zone;

    }

    public static Zone.Member createMember() {
        return new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME);
    }

    public static Zone.Member createSecondMember() {
        return new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME);
    }

    public static Zone createZone() {
        Zone zone = new Zone();
        zone.setCreatedAt(LocalDateTime.now());
        zone.setName(ZONE_TEST_NAME);
        zone.setId(ZONE_TEST_ID);
        zone.setOwnerId(OWNER_TEST_ID);
        zone.setDescription(ZONE_TEST_DESCRIPTION);
        zone.setMembersList(List.of(createMember()));
        return zone;
    }

    public static NewZoneResponse createNewZoneResponse() {
        return new NewZoneResponse(createZone());
    }

    public static GetZoneResponse createGetZoneResponse() {
        return new GetZoneResponse(createZone());
    }

    public static DeletedZoneResponse createDeletedZoneResponse() {
        DeletedZoneResponse deletedZoneResponse = new DeletedZoneResponse(createZone());
        deletedZoneResponse.setDeletedAt(LocalDateTime.now());
        return deletedZoneResponse;
    }

    public static UpdateZoneResponse createUpdateZoneResponse() {
        UpdateZoneResponse updateZoneResponse = new UpdateZoneResponse(createZone());
        return updateZoneResponse;
    }

    public static ZonesResponse createZonesResponse() {
        ZonesResponse zonesResponse = new ZonesResponse(List.of(createZone()));
        return zonesResponse;
    }

    public static MemberTemplate createMemberTemplate() {
        return new MemberTemplate(MEMBER_TEST_ID);
    }

}
