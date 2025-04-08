package aj.FiTracker.FiTracker.Controllers;


import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.NewZoneResponse;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Services.ZoneService;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import aj.FiTracker.FiTracker.enums.MemberRole;
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

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.ZONE_TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("Should add new zone")
    public void testCreateZone() {
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        when(this.zoneServiceMock.addNewZone(eq(newZoneRequest), any(Authentication.class))).thenReturn(zone);
        ResponseEntity<NewZoneResponse> response = this.zoneController.createZone(this.authenticationMock, newZoneRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
