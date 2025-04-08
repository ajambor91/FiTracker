package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import aj.FiTracker.FiTracker.enums.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
public class ZoneServiceIntegrationTest extends AbstractIntegrationTest {

    private final ZoneService zoneService;

    @Autowired
    public ZoneServiceIntegrationTest(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @BeforeEach
    public void setup() {

    }

    @Test
    @DisplayName("Should create new zone")
    public void testAddNewZone() {
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        Zone zone = zoneService.addNewZone(ZoneFactory.createNewZoneTestRequest(), authenticationMock);
        assertNotNull(zone);
    }

    @Test
    @DisplayName("Should get zone")
    public void testGetExistingZoneById() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        Zone zoneFromDB = zoneService.getExistingZoneById(ZONE_TEST_ID, authenticationMock);
        assertNotNull(zoneFromDB);
    }
}
