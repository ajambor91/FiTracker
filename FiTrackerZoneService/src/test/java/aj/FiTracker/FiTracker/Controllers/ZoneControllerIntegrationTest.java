package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.SecurityUtils.WithMockJwt;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockJwt
@ActiveProfiles("integration")
@Tag("integration")
public class ZoneControllerIntegrationTest extends AbstractIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public ZoneControllerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @AfterEach
    public void cleanDB() {
        this.dropZones();
    }

    @Test
    @DisplayName("Should create new zone")
    public void testAddNewZone() throws Exception {
        this.mockMvc.perform(
                        post("/zones/zone")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                                .content("""
                                        {
                                            "zoneName": "New zone",
                                            "zoneDescription": "Description"}                                
                                        """)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.zoneName").value("New zone"))
                .andExpect(jsonPath("$.zoneDescription").value("Description"))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.membersList[0].userId").value(1))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value("Test User"))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").isString())
                .andExpect(jsonPath("$.zoneId").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should throw bad request when payload is invalid")
    public void testAddNewZoneValidationError() throws Exception {
        this.mockMvc.perform(
                        post("/zones/zone")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                                .content("""
                                        {
                                            "zoneDescription": "Opis"
                                            }                                
                                        """)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone"));
    }

    @Test
    @DisplayName("Should return zone")
    public void testGetZone() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        get("/zones/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneName").value(ZONE_TEST_NAME))
                .andExpect(jsonPath("$.zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zoneId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return NotFound, when zone id is null")
    public void testGetZoneIdNull() throws Exception {
        this.mockMvc.perform(
                        get("/zones/zone/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No static resource zones/zone."))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone/"));
    }


    @Test
    @DisplayName("Should throw not found, when zone does not exist")
    public void testGetZoneThrowNotFound() throws Exception {
        this.mockMvc.perform(
                        get("/zones/zone/67f3b66a59153d2661e64002")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone does not exist"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone/67f3b66a59153d2661e64002"));
    }

    @Test
    @DisplayName("Should set zone as deleted")
    public void testDeleteZone() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        delete("/zones/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneName").value(ZONE_TEST_NAME))
                .andExpect(jsonPath("$.zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zoneId").isNotEmpty())
                .andExpect(jsonPath("$.deletedAt").exists());
    }

    @Test
    @DisplayName("Should return NotFound when document does not exist")
    public void testDeleteZoneNotFound() throws Exception {
        this.mockMvc.perform(
                        delete("/zones/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone does not exist"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone/67f3b66a59153d2661e64002"));
    }

    @Test
    @DisplayName("Should return NotFound when id is null")
    public void testDeleteZoneIdIsNull() throws Exception {
        this.mockMvc.perform(
                        delete("/zones/zone/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No static resource zones/zone."))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone/"));
    }

    @Test
    @DisplayName("Should update zone with new member")
    public void testUpdateZone() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneId": "67f3b66a59153d2661e64002",
                                            "zoneName": "New zone",
                                            "zoneDescription": "Test decscription",
                                            "membersList": [
                                               {
                                                "userId": 1,
                                                "role": "ADMIN",
                                                "name": "Test user"
                                                },
                                                {
                                                "userId": 2,
                                                "role": "MEMBER",
                                                "name": "Test member"
                                                }
                                        
                                            ]
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneName").value(ZONE_TEST_NAME))
                .andExpect(jsonPath("$.zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.membersList[1].userId").value(MEMBER_TEST_ID))
                .andExpect(jsonPath("$.membersList[1].role").value("MEMBER"))
                .andExpect(jsonPath("$.membersList[1].name").value(MEMBER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[1].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zoneId").isNotEmpty());
    }

    @Test
    @DisplayName("Should remove member from zone")
    public void testDeleteZoneMember() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.addMember(new Zone.Member(MEMBER_TEST_ID, MemberRole.MEMBER, MEMBER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/" + ZONE_TEST_ID + "/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneId": "67f3b66a59153d2661e64002",
                                            "zoneName": "New zone",
                                            "zoneDescription": "Test decscription",
                                            "membersList": [
                                                {
                                                "userId": 2,
                                                "role": "MEMBER",
                                                "name": "Test member"
                                                }
                                        
                                            ]
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneName").value(ZONE_TEST_NAME))
                .andExpect(jsonPath("$.zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zoneId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return NotFound when deleting member from Zone and zone does not exist")
    public void testDeleteZoneMemberNotFound() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/xxxx/member").contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneId": "67f3b66a59153d2661e64002",
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription",
                                                          "membersList": [
                                                {
                                                "userId": 2,
                                                "role": "MEMBER",
                                                "name": "Test member"
                                                }
                                        
                                            ]
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone does not exist"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should return NotFound when deleting member from Zone and zone does not exist")
    public void testDeleteZoneMemberBadRequest() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/" + ZONE_TEST_ID + "/member").contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription"
                                        
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should update zone with new name")
    public void testUpdateZoneWithNewName() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneId": "67f3b66a59153d2661e64002",
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription"
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneName").value("New name"))
                .andExpect(jsonPath("$.zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zoneId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return NotFound when updating Zone does not exist")
    public void testUpdateZoneNotFound() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/67f3b66a59153a2661e64002")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneId": "67f3b66a59153d2661e64002",
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription"
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone does not exist"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should return BadRequest when updating Zone id is null")
    public void testUpdateZoneBadRequest() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        patch("/zones/zone/67f3b66a59153a2661e64002")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription"
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should return NotFound when id is null")
    public void testUpdateZoneNotFoundWhenPathIsNull() throws Exception {
        this.mockMvc.perform(
                        patch("/zones/zone/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "zoneName": "New name",
                                            "zoneDescription": "Test decscription"
                                        }
                                        """)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No static resource zones/zone."))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/zones/zone/"));
    }

    @Test
    @DisplayName("Should return last four user zones")
    public void testGetUserZones() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        get("/zones/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zones[0].zoneName").value("New zone"))
                .andExpect(jsonPath("$.zones[0].zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.zones[0].ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.zones[0].membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.zones[0].membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.zones[0].membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.zones[0].membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zones[0].zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zones[0].zoneId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return all found user zones")
    public void testGetUserZonesAll() throws Exception {
        Zone zone = new Zone(ZoneFactory.createNewZoneTestRequest(), OWNER_TEST_ID);
        zone.addMember(new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME));
        zone.setId(ZONE_TEST_ID);
        this.insertTestDataIntoDB(zone);
        this.mockMvc.perform(
                        get("/zones/?all=true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.zones[0].zoneName").value("New zone"))
                .andExpect(jsonPath("$.zones[0].zoneDescription").value(ZONE_TEST_DESCRIPTION))
                .andExpect(jsonPath("$.zones[0].ownerId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.zones[0].membersList[0].userId").value(OWNER_TEST_ID))
                .andExpect(jsonPath("$.zones[0].membersList[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.zones[0].membersList[0].name").value(USER_TEST_NAME))
                .andExpect(jsonPath("$.zones[0].membersList[0].addedAt").exists())
                .andExpect(jsonPath("$.zones[0].zoneId").value(ZONE_TEST_ID))
                .andExpect(jsonPath("$.zones[0].zoneId").isNotEmpty());
    }
}
