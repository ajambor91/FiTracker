package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.Models.MembersTemplate;
import aj.FiTracker.FiTracker.Repositories.ZoneRepository;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ZoneServiceUnitTest {
    private Jwt jwtMock;
    private ZoneService zoneService;
    private ZoneRepository zoneRepositoryMock;
    private KafkaProducerService kafkaProducerServiceMock;
    private Authentication authenticationMock;

    @BeforeEach
    public void setup() {
        this.kafkaProducerServiceMock = mock(KafkaProducerService.class);

        this.zoneRepositoryMock = mock(ZoneRepository.class);
        this.zoneService = new ZoneService(zoneRepositoryMock, this.kafkaProducerServiceMock);
    }

    @Test
    @DisplayName("Should return new Zone")
    public void testAddNoweZone() throws JsonProcessingException {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        testZone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        when(this.zoneRepositoryMock.save(any(Zone.class))).thenReturn(testZone);
        NewZoneResponse zone = this.zoneService.addNewZone(newZoneRequest, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(OWNER_TEST_ID, zone.getOwnerId());
        zone.getMembersList().forEach(member -> {
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertNotNull(member.getAddedAt());
        });
        verify(this.zoneRepositoryMock, times(1)).save(any(Zone.class));
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.kafkaProducerServiceMock).sendNewMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
        assertEquals(OWNER_TEST_ID, membersTemplate.getMembersList().getFirst().memberId());
    }

    @Test
    @DisplayName("Should throw InternalServerException")
    public void testAddNoweZoneInternalServerException() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.save(any(Zone.class))).thenThrow(new RuntimeException("Boom!"));

        InternalServerException internalServerException = assertThrows(InternalServerException.class, () -> {
            this.zoneService.addNewZone(newZoneRequest, this.authenticationMock);
        });
        assertEquals("Boom!", internalServerException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).save(any(Zone.class));

    }


    @Test
    @DisplayName("Should throw ZoneAlreadyExistsException for duplicated ids")
    public void testAddNoweZoneZoneDoesntExistException() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.save(any(Zone.class))).thenThrow(DuplicateKeyException.class);

        ZoneAlreadyExistsException zoneAlreadyExistsException = assertThrows(ZoneAlreadyExistsException.class, () -> {
            this.zoneService.addNewZone(newZoneRequest, this.authenticationMock);
        });
        verify(this.zoneRepositoryMock, times(1)).save(any(Zone.class));

    }

    @Test
    @DisplayName("Should return existing Zone")
    public void testGetExistingZoneById() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.of(testZone));
        GetZoneResponse zone = this.zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(OWNER_TEST_ID, zone.getOwnerId());
        zone.getMembersList().forEach(member -> {
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertNotNull(member.getAddedAt());
        });
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));
    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException existing Zone")
    public void testGetExistingZoneByIdNotFound() {
        this.createAuthMock();
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.empty());
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));

    }

    @Test
    @DisplayName("Should update Zone and return")
    public void testUpdateZone() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.of(testZone));
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, authenticationMock, updateZoneRequest);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(OWNER_TEST_ID, zone.getOwnerId());
        zone.getMembersList().forEach(member -> {
            assertEquals(MemberRole.ADMIN, member.getRole());
            assertEquals(USER_TEST_NAME, member.getName());
            assertEquals(OWNER_TEST_ID, member.getUserId());
            assertNotNull(member.getAddedAt());
        });
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));

    }

    @Test
    @DisplayName("Should update zone with new member and return")
    public void testUpdateZoneWithNewMember() throws JsonProcessingException {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithMember();

        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        List<Zone.Member> membersList = new ArrayList<>();
        membersList.add(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        testZone.setMembersList(membersList);
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.of(testZone));
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, authenticationMock, updateZoneRequest);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
        assertEquals(OWNER_TEST_ID, zone.getOwnerId());
        assertEquals(1, zone.getMembersList().size());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.kafkaProducerServiceMock, times(1)).sendNewMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
        assertEquals(MEMBER_TEST_ID, membersTemplate.getMembersList().getFirst().memberId());
        argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.kafkaProducerServiceMock, times(1)).sendDeletedMembers(argumentCaptor.capture());
        membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
        assertEquals(OWNER_TEST_ID, membersTemplate.getMembersList().getFirst().memberId());
    }

    @Test
    @DisplayName("Should update zone with new name and description and then return")
    public void testUpdateZoneWithNewNameAndDescription() throws JsonProcessingException {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        UpdateZoneRequest updateZoneRequest = ZoneFactory.createUpdateZoneTestRequestWithNameAndDescription();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.of(testZone));
        UpdateZoneResponse zone = this.zoneService.updateZone(ZONE_TEST_ID, authenticationMock, updateZoneRequest);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME_SECOND, zone.getZoneName());
        assertEquals(ZONE_TEST_DESCRIPTION_SECOND, zone.getZoneDescription());
        assertEquals(OWNER_TEST_ID, zone.getOwnerId());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));
        verify(this.kafkaProducerServiceMock, never()).sendDeletedMembers(any(MembersTemplate.class));

    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException updating Zone")
    public void testUpdateZonedNotFound() throws JsonProcessingException {
        this.createAuthMock();
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.empty());
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));
        verify(this.kafkaProducerServiceMock, never()).sendDeletedMembers(any(MembersTemplate.class));

    }

    @Test
    @DisplayName("Should throw InternalServerException existing Zone")
    public void testUpdateZoneInternalServerError() throws JsonProcessingException {
        this.createAuthMock();
        when(this.zoneRepositoryMock.findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException internalServerException = assertThrows(InternalServerException.class, () -> {
            this.zoneService.getExistingZoneById(ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Boom!", internalServerException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndDeletedAtIsNullAndMembersList_UserId(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID));
        verify(this.kafkaProducerServiceMock, never()).sendDeletedMembers(any(MembersTemplate.class));


    }

    @Test
    @DisplayName("Should return all zones")
    public void testGetAllZones() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        when(this.zoneRepositoryMock.findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID))).thenReturn(List.of(testZone));
        ZonesResponse zonesResponse = this.zoneService.getAllZones(authenticationMock);
        assertInstanceOf(ZonesResponse.class, zonesResponse);
        List<ZonesResponse.ZoneResponse> zones = zonesResponse.getZones();
        zones.forEach(zone -> {
            assertEquals(ZONE_TEST_ID, zone.getZoneId());
            assertEquals(ZONE_TEST_NAME, zone.getZoneName());
            assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
            assertEquals(OWNER_TEST_ID, zone.getOwnerId());
            zone.getMembersList().forEach(member -> {
                assertEquals(MemberRole.ADMIN, member.getRole());
                assertEquals(USER_TEST_NAME, member.getName());
                assertEquals(OWNER_TEST_ID, member.getUserId());
                assertNotNull(member.getAddedAt());
            });
        });
        verify(this.zoneRepositoryMock, times(1)).findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID));

    }

    @Test
    @DisplayName("Should return last four zones")
    public void testGetLastFourZones() {
        this.createAuthMock();
        NewZoneRequest newZoneRequest = ZoneFactory.createNewZoneTestRequest();
        Zone testZone = new Zone(newZoneRequest, OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(this.zoneRepositoryMock.findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID), eq(pageable))).thenReturn(List.of(testZone));
        ZonesResponse zonesResponse = this.zoneService.getLastFourZones(authenticationMock);
        assertInstanceOf(ZonesResponse.class, zonesResponse);
        List<ZonesResponse.ZoneResponse> zones = zonesResponse.getZones();
        zones.forEach(zone -> {
            assertEquals(ZONE_TEST_ID, zone.getZoneId());
            assertEquals(ZONE_TEST_NAME, zone.getZoneName());
            assertEquals(ZONE_TEST_DESCRIPTION, zone.getZoneDescription());
            assertEquals(OWNER_TEST_ID, zone.getOwnerId());
            zone.getMembersList().forEach(member -> {
                assertEquals(MemberRole.ADMIN, member.getRole());
                assertEquals(USER_TEST_NAME, member.getName());
                assertEquals(OWNER_TEST_ID, member.getUserId());
                assertNotNull(member.getAddedAt());
            });
        });
        verify(this.zoneRepositoryMock, times(1)).findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID), any(Pageable.class));

    }

    @Test
    @DisplayName("Should return empty zones list when anyone was not found")
    public void testGetAllZonesEmptyList() {
        this.createAuthMock();
        when(this.zoneRepositoryMock.findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID))).thenReturn(List.of());
        ZonesResponse zonesResponse = this.zoneService.getAllZones(authenticationMock);
        assertInstanceOf(ZonesResponse.class, zonesResponse);
        assertEquals(0, zonesResponse.getZones().size());
        verify(this.zoneRepositoryMock, times(1)).findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID));

    }

    @Test
    @DisplayName("Should return throw InternalServerException")
    public void testGetAllZonesEmptyListInternalServerException() {
        this.createAuthMock();
        when(this.zoneRepositoryMock.findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException internalServerException = assertThrows(InternalServerException.class, () -> {
            this.zoneService.getAllZones(this.authenticationMock);
        });
        assertEquals("Boom!", internalServerException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).findByDeletedAtIsNullAndMembersList_UserId(eq(OWNER_TEST_ID));

    }

    @Test
    @DisplayName("Should remove user from zone")
    public void testRemoveZoneMember() throws JsonProcessingException {
        this.createAuthMock();
        Zone testZone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        testZone.setId(ZONE_TEST_ID);
        testZone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        testZone.addMember(new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME));
        RemoveZoneMemberRequest removeZoneMemberRequest = ZoneFactory.createRemoveZoneMemberRequest();
        when(this.zoneRepositoryMock.findByIdAndOwnerIdAndDeletedAtIsNull(eq(ZONE_TEST_ID), eq(OWNER_TEST_ID))).thenReturn(Optional.of(testZone));
        UpdateZoneResponse zone = this.zoneService.removeZoneMember(ZONE_TEST_ID, removeZoneMemberRequest, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, zone.getZoneId());
        assertEquals(ZONE_TEST_NAME, zone.getZoneName());
        assertEquals(1, zone.getMembersList().size());
        assertNotEquals(MEMBER_TEST_ID, zone.getMembersList().getFirst().getUserId());
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.kafkaProducerServiceMock).sendDeletedMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
        assertEquals(MEMBER_TEST_ID, membersTemplate.getMembersList().getFirst().memberId());

    }

    @Test
    @DisplayName("Should throw ZoneDoesntExistException when removing member from zone")
    public void testRemoveZoneMemberZoneDoesntExistException() throws JsonProcessingException {
        this.createAuthMock();
        RemoveZoneMemberRequest removeZoneMemberRequest = ZoneFactory.createRemoveZoneMemberRequest();

        when(this.zoneRepositoryMock.findByIdAndOwnerIdAndDeletedAtIsNull(any(String.class), anyLong())).thenReturn(Optional.empty());
        ZoneDoesntExistException zoneDoesntExistException = assertThrows(ZoneDoesntExistException.class, () -> {
            this.zoneService.removeZoneMember(ZONE_TEST_ID, removeZoneMemberRequest, this.authenticationMock);
        });

        assertEquals("Zone does not exist", zoneDoesntExistException.getMessage());
        verify(this.zoneRepositoryMock, times(1)).findByIdAndOwnerIdAndDeletedAtIsNull(any(String.class), anyLong());
        verify(this.kafkaProducerServiceMock, never()).sendDeletedMembers(any(MembersTemplate.class));
    }

    @Test
    @DisplayName("Should remove user from all zones")
    public void testRemoveMemberFromAllZone() throws JsonProcessingException {
        MemberTemplate memberTemplate = ZoneFactory.createMemberTemplate();
        this.zoneService.removeMemberFromAllZone(memberTemplate);
        verify(this.zoneRepositoryMock, times(1)).pullMemberFromAllZones(eq(MEMBER_TEST_ID));
        verify(this.zoneRepositoryMock, times(1)).deleteZonesWithOneMember();
    }

    private void createAuthMock() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(OWNER_TEST_ID));
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(USER_TEST_NAME);
        this.authenticationMock = mock(Authentication.class);
        when(this.authenticationMock.getPrincipal()).thenReturn(this.jwtMock);
    }
}
