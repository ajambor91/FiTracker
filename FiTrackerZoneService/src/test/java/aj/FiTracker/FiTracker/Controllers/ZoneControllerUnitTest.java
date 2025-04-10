package aj.FiTracker.FiTracker.Controllers;


import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Services.ZoneService;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.ZONE_TEST_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class ZoneControllerUnitTest {
    private ZoneService zoneServiceMock;
    private ZoneController zoneController;
    private Authentication authenticationMock;
    @BeforeEach
    public void setup() {
        this.authenticationMock = mock(Authentication.class);
        this.zoneServiceMock = mock(ZoneService.class);
        this.zoneController = new ZoneController(this.zoneServiceMock);
    }

    @Test
    @DisplayName("Should return new Zone")
    public void testCreateZone() {
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        zone.setCreatedAt(LocalDateTime.now());
        when(this.zoneServiceMock.addNewZone(eq(newZoneRequest), any(Authentication.class))).thenReturn(zone);
        ResponseEntity<NewZoneResponse> response = this.zoneController.createZone(this.authenticationMock, newZoneRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ZONE_TEST_ID, response.getBody().getZoneId());
        assertEquals(ZONE_TEST_DESCRIPTION, response.getBody().getZoneDescription());
        assertNotNull(response.getBody().getCreatedAt());
        assertEquals(OWNER_TEST_ID, response.getBody().getOwnerId());
        response.getBody().getMemberList().forEach(member -> {
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertNotNull(member.getAddedAt());
        });

        verify(this.zoneServiceMock, times(1)).addNewZone(eq(newZoneRequest), any(Authentication.class));
    }
    
    @Test
    @DisplayName("Should return found Zone")
    public void testGetZone() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        zone.setCreatedAt(LocalDateTime.now());
        when(this.zoneServiceMock.getExistingZoneById(eq(ZONE_TEST_ID), any(Authentication.class))).thenReturn(zone);
        ResponseEntity<GetZoneResponse> response = this.zoneController.getZone(this.authenticationMock, ZONE_TEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ZONE_TEST_ID, response.getBody().getZoneId());
        assertEquals(ZONE_TEST_DESCRIPTION, response.getBody().getZoneDescription());
        assertNotNull(response.getBody().getCreatedAt());
        assertEquals(OWNER_TEST_ID, response.getBody().getOwnerId());
        response.getBody().getMemberList().forEach(member -> {
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertNotNull(member.getAddedAt());
        });

        verify(this.zoneServiceMock, times(1)).getExistingZoneById(eq(ZONE_TEST_ID), any(Authentication.class));

    }

    @Test
    @DisplayName("Should return deleted Zone")
    public void testDeleteZone() {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        zone.setCreatedAt(LocalDateTime.now());
        zone.setDeletedAt(LocalDateTime.now());
        when(this.zoneServiceMock.removeZoneById(eq(ZONE_TEST_ID), any(Authentication.class))).thenReturn(zone);
        ResponseEntity<DeletedZoneResponse> response = this.zoneController.deleteZone(this.authenticationMock, ZONE_TEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ZONE_TEST_ID, response.getBody().getZoneId());
        assertEquals(ZONE_TEST_DESCRIPTION, response.getBody().getZoneDescription());
        assertNotNull(response.getBody().getCreatedAt());
        assertNotNull(response.getBody().getDeletedAt());
        assertEquals(OWNER_TEST_ID, response.getBody().getOwnerId());
        response.getBody().getMemberList().forEach(member -> {
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertNotNull(member.getAddedAt());
        });

        verify(this.zoneServiceMock, times(1)).removeZoneById(eq(ZONE_TEST_ID), any(Authentication.class));

    }

    @Test
    @DisplayName("Should return patched Zone")
    public void testUpdateZone() {
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequest();
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        zone.setCreatedAt(LocalDateTime.now());
        when(this.zoneServiceMock.updateZone(eq(ZONE_TEST_ID), any(Authentication.class), eq(updateZoneRequest))).thenReturn(zone);
        ResponseEntity<UpdateZoneResponse> response = this.zoneController.updateZone(this.authenticationMock,updateZoneRequest, ZONE_TEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ZONE_TEST_ID, response.getBody().getZoneId());
        assertEquals(ZONE_TEST_DESCRIPTION, response.getBody().getZoneDescription());
        assertNotNull(response.getBody().getCreatedAt());
        assertEquals(OWNER_TEST_ID, response.getBody().getOwnerId());
        response.getBody().getMemberList().forEach(member -> {
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertNotNull(member.getAddedAt());
        });
        verify(this.zoneServiceMock, times(1)).updateZone(eq(ZONE_TEST_ID), any(Authentication.class), eq(updateZoneRequest));

    }

    @Test
    @DisplayName("Should remove member from Zone")
    public void testDeleteZoneMember() {
        RemoveZoneMemberRequest removeZoneMemberRequest = ZoneFactory.createRemoveZoneMemberRequest();
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        zone.setCreatedAt(LocalDateTime.now());
        when(this.zoneServiceMock.removeZoneMember(eq(ZONE_TEST_ID), eq(removeZoneMemberRequest), any(Authentication.class))).thenReturn(zone);
        ResponseEntity<UpdateZoneResponse> response = this.zoneController.deleteZoneMember(this.authenticationMock,removeZoneMemberRequest, ZONE_TEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ZONE_TEST_ID, response.getBody().getZoneId());
        assertEquals(ZONE_TEST_DESCRIPTION, response.getBody().getZoneDescription());
        assertNotNull(response.getBody().getCreatedAt());
        assertEquals(OWNER_TEST_ID, response.getBody().getOwnerId());
        assertEquals(1, response.getBody().getMemberList().size());
        Zone.Member member = response.getBody().getMemberList().getFirst();
        assertEquals(OWNER_TEST_ID, member.getUserId());
        assertEquals(MemberRole.ADMIN, member.getRole());
        assertEquals(USER_TEST_NAME, member.getName());
        assertNotNull(member.getAddedAt());
        verify(this.zoneServiceMock, times(1)).removeZoneMember(eq(ZONE_TEST_ID), eq(removeZoneMemberRequest), any(Authentication.class));

    }

    @Test
    @DisplayName("Should return found Zones array")
    public void testGetAllUserZones() {
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequest();
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        when(this.zoneServiceMock.getAllZones(any(Authentication.class))).thenReturn(Arrays.asList(zone));
        ResponseEntity<ZonesResponse> response = this.zoneController.getAllUserZones(this.authenticationMock);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(List.class, response.getBody().getZones());
        verify(this.zoneServiceMock, times(1)).getAllZones(any(Authentication.class));

    }

}
