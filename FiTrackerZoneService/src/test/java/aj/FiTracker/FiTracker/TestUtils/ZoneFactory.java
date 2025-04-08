package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;

public final class ZoneFactory {

    public static String ZONE_TEST_ID = "67f3b66a59153d2661e64002";
    public static String ZONE_TEST_NAME = "New zone";
    public static String ZONE_TEST_DESCRIPTION = "Test decscription";

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

}
