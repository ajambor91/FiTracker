package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
public class ZoneServiceIntegrationTest extends AbstractIntegrationTest {

    private final ZoneService zoneService;
    private Authentication authenticationMock;
    private Jwt jwtMock;

    @Autowired
    public ZoneServiceIntegrationTest(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @BeforeEach
    public void setup() {

        this.authenticationMock = mock(Authentication.class);
    }

    @AfterEach
    public void cleanAfterTest() {
        this.dropZones();
    }

    @Test
    @DisplayName("Should create new zone")
    public void testAddNewZone() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        NewZoneResponse zone = zoneService.addNewZone(ZoneFactory.createNewZoneTestRequest(), this.authenticationMock);
        assertNotNull(zone);
        assertNotNull(zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
    }

    @Test
    @DisplayName("Should get zone")
    public void testGetExistingZoneById() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        GetZoneResponse zoneFromDB = zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        assertNotNull(zoneFromDB);
        assertEquals(ZONE_TEST_ID, zoneFromDB.getZoneId());
        assertEquals(ZONE_TEST_NAME, zoneFromDB.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zoneFromDB.getZoneDescription());
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when getting zone")
    public void testGetExistingZoneByIdZoneDoesntExistException() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    @Test
    @DisplayName("Should set zone as deleted and return")
    public void testRemoveZoneById() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        DeletedZoneResponse deletedZone = this.zoneService.removeZoneById(ZONE_TEST_ID, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, deletedZone.getZoneId());
        assertEquals(ZONE_TEST_NAME, deletedZone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, deletedZone.getZoneDescription());
        assertNotNull(deletedZone.getDeletedAt());
    }


    @Test
    @DisplayName("Should throw ZoneDoesntExistException when deleting Zone")
    public void testRemoveZoneByIdZoneDoesntExistException() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.removeZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    @Test
    @DisplayName("Should update Zone and then return")
    public void testUpdateZone() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequest();
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());

    }

    @Test
    @DisplayName("Should update Zone and then return with new members")
    public void testUpdateZoneWithMembers() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithMember();
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(1, zone.getMembersList().size());
    }

    @Test
    @DisplayName("Should update Zone and then return")
    public void testUpdateZoneWithNewNameAndDescription() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithNameAndDescription();
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME_SECOND, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION_SECOND, zone.getZoneDescription());
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when updating zone")
    public void testUpdateZoneZoneDoesntExistException() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, ZoneFactory.createUpdateZoneTestRequest());
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    @Test
    @DisplayName("Should remove members from zone")
    public void testRemoveZoneMember() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.insertTestZoneIntoDB();
        UpdateZoneResponse zone = this.zoneService.removeZoneMember(ZONE_TEST_ID, ZoneFactory.createRemoveZoneMemberRequest(), authenticationMock);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(1, zone.getMembersList().size());
        assertEquals(OWNER_TEST_ID, zone.getMembersList().getFirst().getUserId());
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when removing zone can not be found")
    public void testRemoveZoneMemberZoneDoesntExistException() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.removeZoneMember(ZONE_TEST_ID, ZoneFactory.createRemoveZoneMemberRequest(), this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    private void insertTestZoneIntoDB() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestZoneWithAdditionalMemberIntoDB();

    }

    private void insertTestZoneWithAdditionalMemberIntoDB() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.addMember(new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
    }

}
