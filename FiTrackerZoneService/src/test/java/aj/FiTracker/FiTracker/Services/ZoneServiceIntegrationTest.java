package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import aj.FiTracker.FiTracker.enums.MemberRole;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;
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
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(ZONE_TEST_NAME);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.authenticationMock.getPrincipal()).thenReturn(jwtMock);
        this.authenticationMock = mock(Authentication.class);
    }

    @AfterEach
    public void cleanAfterTest() {
        this.dropZones();
    }

    @Test
    @DisplayName("Should create new zone")
    public void testAddNewZone() {

        Zone zone = zoneService.addNewZone(ZoneFactory.createNewZoneTestRequest(), this.authenticationMock);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getId());
        assertEquals(ZONE_TEST_NAME, zone.getName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getDescription());
    }

    @Test
    @DisplayName("Should throw ZoneAlreadyExistsException when adding new zone")
    public void testAddNewZoneDuplicateKeyException() {
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        this.insertTestZoneIntoDB();
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        assertThrows(ZoneAlreadyExistsException.class, () -> {
            zoneService.addNewZone(newZoneRequest, this.authenticationMock);
        });
    }


    @Test
    @DisplayName("Should get zone")
    public void testGetExistingZoneById() {
        this.insertTestZoneIntoDB();
        Zone zoneFromDB = zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        assertNotNull(zoneFromDB);
        assertEquals(ZONE_TEST_ID, zoneFromDB.getId());
        assertEquals(ZONE_TEST_NAME, zoneFromDB.getName());
        assertEquals(ZONE_TEST_DESCRIPTION, zoneFromDB.getDescription());
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when getting zone")
    public void testGetExistingZoneByIdZoneDoesntExistException() {
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    @Test
    @DisplayName("Should set zone as deleted and return")
    public void testRemoveZoneById() {
        this.insertTestZoneIntoDB();
        Zone deletedZone = this.zoneService.removeZoneById(ZONE_TEST_ID, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, deletedZone.getId());
        assertEquals(ZONE_TEST_NAME, deletedZone.getName());
        assertEquals(ZONE_TEST_DESCRIPTION, deletedZone.getDescription());
        assertNotNull(deletedZone.getDeletedAt());
    }


    @Test
    @DisplayName("Should throw ZoneDoesntExistException when deleting Zone")
    public void testRemoveZoneByIdZoneDoesntExistException() {
        this.insertTestZoneIntoDB();
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.removeZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    @Test
    @DisplayName("Should update Zone and then return")
    public void testUpdateZone() {
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequest();
        Zone zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getId());
        assertEquals(ZONE_TEST_NAME, zone.getName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getDescription());

    }

    @Test
    @DisplayName("Should update Zone and then return")
    public void testUpdateZoneWithMembers() {
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithMember();
        Zone zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getId());
        assertEquals(ZONE_TEST_NAME, zone.getName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getDescription());
        assertEquals(2, zone.getMembers().size());
    }

    @Test
    @DisplayName("Should update Zone and then return")
    public void testUpdateZoneWithNewNameAndDescription() {
        this.insertTestZoneIntoDB();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithNameAndDescription();
        Zone zone = this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, updateZoneRequest);
        assertNotNull(zone);
        assertEquals(ZONE_TEST_ID, zone.getId());
        assertEquals(ZONE_TEST_NAME_SECOND, zone.getName());
        assertEquals(ZONE_TEST_DESCRIPTION_SECOND, zone.getDescription());
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when updating zone")
    public void testUpdateZoneZoneDoesntExistException() {
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.updateZone(ZONE_TEST_ID, this.authenticationMock, ZoneFactory.createUpdateZoneTestRequest());
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
    }

    private void insertTestZoneIntoDB() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
    }
}
