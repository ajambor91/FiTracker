package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Models.MembersTemplate;
import aj.FiTracker.FiTrackerExpenses.Repositories.MembersRepository;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@Tag("integration")
@SpringBootTest
public class MembersServiceIntegrationTest extends AbstractIntegrationTest {

    private final MembersService membersService;
    private final MembersRepository membersRepository;

    @Autowired
    public MembersServiceIntegrationTest(MembersService membersService, MembersRepository membersRepository) {
        this.membersService = membersService;
        this.membersRepository = membersRepository;
    }

    @AfterEach
    public void cleanAfterTest() {
        this.truncateTable("app_data.app_user");
    }

    @Test
    @DisplayName("Should add new members")
    public void testAddNewMembers() {

        MembersTemplate membersTemplate = TestUtils.createMemberTemplate();
        MembersTemplate.Member member = membersTemplate.getMembersList().getFirst();
        MembersTemplate.Member anotherMember = membersTemplate.getMembersList().getLast();
        this.membersService.addNewMembers(membersTemplate);
        List<Map<String, Object>> members = this.getTestData("SELECT app_data.app_user.id as id, app_data.app_user.app_user_id as userId, app_data.app_user.app_zone_id as zoneId " +
                "FROM app_data.app_user WHERE app_data.app_user.app_user_id = ?", new Long[]{member.memberId()});
        Map<String, Object> memberFromDb = members.getFirst();

        assertEquals(member.memberId(), memberFromDb.get("userid"));
        assertEquals(membersTemplate.getZoneId(), memberFromDb.get("zoneid"));

        List<Map<String, Object>> anotherMembers = this.getTestData("SELECT app_data.app_user.id as id, app_data.app_user.app_user_id as userId, app_data.app_user.app_zone_id as zoneId " +
                "FROM app_data.app_user WHERE app_data.app_user.app_user_id = ?", new Long[]{anotherMember.memberId()});
        Map<String, Object> anotherMemberFromDb = anotherMembers.getFirst();

        assertEquals(anotherMember.memberId(), anotherMemberFromDb.get("userid"));
        assertEquals(membersTemplate.getZoneId(), anotherMemberFromDb.get("zoneid"));
    }

    @Test
    @DisplayName("Should remove members")
    public void testRemoveMembers() {
        this.insertTestUsers();
        MembersTemplate membersTemplate = TestUtils.createMemberTemplateOneMember();
        MembersTemplate.Member member = membersTemplate.getMembersList().getFirst();
        this.membersService.removeMembers(membersTemplate);
        List<Map<String, Object>> members = this.getTestData("SELECT app_data.app_user.id as id, app_data.app_user.app_user_id as userId, app_data.app_user.app_zone_id as zoneId " +
                "FROM app_data.app_user WHERE app_data.app_user.app_user_id = ?", new Long[]{member.memberId()});
        assertTrue(members.isEmpty());
        List<Map<String, Object>> anotherMembers = this.getTestData("SELECT app_data.app_user.id as id, app_data.app_user.app_user_id as userId, app_data.app_user.app_zone_id as zoneId " +
                "FROM app_data.app_user WHERE app_data.app_user.app_user_id = ?", new Long[]{2L});
        Map<String, Object> memberFromDb = anotherMembers.getFirst();
        assertEquals(MEMBER_TEST_ID_SECOND, memberFromDb.get("userid"));
        assertEquals("abc", memberFromDb.get("zoneid"));
    }

    @Test
    @DisplayName("Get user by memberId and zoneId")
    public void testGetUserByZoneIdAndId() {
        this.insertTestUsers();
        User user = this.membersService.getUserByZoneIdAndId(USER_TEST_ID, ZONE_TEST_ID);
        assertEquals(USER_TEST_ID, user.getUserId());
        assertEquals(ZONE_TEST_ID, user.getZoneId());
    }

    @Test
    @DisplayName("Should throw unauthorized when user for zone is not found")
    public void testGetUserByZoneIdAndIdUnauthorized() {
        UserUnauthorizedException user = assertThrows(UserUnauthorizedException.class, () -> {
            this.membersService.getUserByZoneIdAndId(USER_TEST_ID, ZONE_TEST_ID);
        });
        assertEquals("User does not have privileges to zone", user.getMessage());
    }

    @Test
    @DisplayName("Should delete Member from all zones")
    public void testRemoveMember() {
        this.insertTestUsers();
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (3, " + USER_TEST_ID + ", 'ANOTHER_ZONE_ID');");
        this.membersService.removeMember(new MemberTemplate(USER_TEST_ID));
        List<Map<String, Object>> members = this.getTestData("SELECT app_data.app_user.id as id, app_data.app_user.app_user_id as userId, app_data.app_user.app_zone_id as zoneId " +
                "FROM app_data.app_user WHERE app_data.app_user.app_user_id = " + USER_TEST_ID);
        assertTrue(members.isEmpty());

    }

    private void insertTestUsers() {
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (1, " + USER_TEST_ID + ", '" + ZONE_TEST_ID + "');");
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (2, " + MEMBER_TEST_ID_SECOND + ", '" + ZONE_TEST_ID + "');");

    }
}
