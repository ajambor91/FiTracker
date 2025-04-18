package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Repositories.MembersRepository;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class MembersServiceUnitTest {

    private MembersService membersService;
    private MembersRepository membersRepositoryMock;

    @BeforeEach
    public void setup() {
        this.membersRepositoryMock = mock(MembersRepository.class);
        this.membersService = new MembersService(this.membersRepositoryMock);
    }

    @Test
    @DisplayName("Should add members")
    public void testAddNewMembers() {
        MemberTemplate memberTemplate = TestUtils.createMemberTemplate();
        this.membersService.addNewMembers(memberTemplate);
        ArgumentCaptor<List<User>> captor = ArgumentCaptor.forClass(List.class);
        verify(this.membersRepositoryMock, times(1)).saveAll(captor.capture());
        List<User> captorValue = captor.getValue();
        assertInstanceOf(List.class, captorValue);
        assertEquals(2, captorValue.size());
        User userFirst = captorValue.getFirst();
        User userSecond = captorValue.getLast();
        assertEquals(ZONE_TEST_ID, userFirst.getZoneId());
        assertEquals(ZONE_TEST_ID, userSecond.getZoneId());
        assertEquals(MEMBER_TEST_ID, userFirst.getUserId());
        assertEquals(MEMBER_TEST_ID_SECOND, userSecond.getUserId());

    }

    @Test
    @DisplayName("Should throw InternalServerException when adding members")
    public void testAddNewMembersInternalServerException() {
        MemberTemplate memberTemplate = TestUtils.createMemberTemplate();
        when(this.membersRepositoryMock.saveAll(any(List.class))).thenThrow(RuntimeException.class);
        InternalServerException internalServerException = assertThrows(InternalServerException.class, () -> {
            this.membersService.addNewMembers(memberTemplate);
            ArgumentCaptor<List<User>> captor = ArgumentCaptor.forClass(List.class);
            verify(this.membersRepositoryMock, times(1)).saveAll(captor.capture());
            List<User> captorValue = captor.getValue();
            assertInstanceOf(List.class, captorValue);
            assertEquals(2, captorValue.size());
            User userFirst = captorValue.getFirst();
            User userSecond = captorValue.getLast();
            assertEquals(ZONE_TEST_ID, userFirst.getZoneId());
            assertEquals(ZONE_TEST_ID, userSecond.getZoneId());
            assertEquals(MEMBER_TEST_ID, userFirst.getUserId());
            assertEquals(MEMBER_TEST_ID_SECOND, userSecond.getUserId());
        });
        assertInstanceOf(InternalServerException.class, internalServerException);
    }

    @Test
    @DisplayName("Should return user by zoneId and userId ")
    public void testGetUserByZoneIdAndId() {
        User testUser = new User(MEMBER_TEST_ID, ZONE_TEST_ID);
        when(this.membersRepositoryMock.findByUserIdAndZoneId(eq(MEMBER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(Optional.of(testUser));
        User user = this.membersService.getUserByZoneIdAndId(MEMBER_TEST_ID, ZONE_TEST_ID);
        verify(this.membersRepositoryMock, times(1)).findByUserIdAndZoneId(eq(MEMBER_TEST_ID), eq(ZONE_TEST_ID));
        assertInstanceOf(User.class, user);
        assertEquals(MEMBER_TEST_ID, user.getUserId());
        assertEquals(ZONE_TEST_ID, user.getZoneId());
    }

    @Test
    @DisplayName("Should throw user UserUnauthorizedException when cannot find user ")
    public void testGetUserByZoneIdAndIdUserUnauthorizedException() {
        when(this.membersRepositoryMock.findByUserIdAndZoneId(eq(MEMBER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(Optional.empty());
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.membersService.getUserByZoneIdAndId(MEMBER_TEST_ID, ZONE_TEST_ID);
            verify(this.membersRepositoryMock, times(1)).findByUserIdAndZoneId(eq(MEMBER_TEST_ID), eq(ZONE_TEST_ID));

        });
        assertInstanceOf(UserUnauthorizedException.class, exception);
        assertEquals("User does not have privileges to zone", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove users")
    public void testRemoveMembers() {
        MemberTemplate memberTemplate = TestUtils.createMemberTemplateOneMember();
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        this.membersService.removeMembers(memberTemplate);
        verify(this.membersRepositoryMock, times(1)).removeMember(captor.capture(), stringCaptor.capture());
        String zoneId = stringCaptor.getValue();
        long userId = captor.getValue();
        assertEquals(ZONE_TEST_ID, zoneId);
        assertEquals(MEMBER_TEST_ID, userId);
    }
}
