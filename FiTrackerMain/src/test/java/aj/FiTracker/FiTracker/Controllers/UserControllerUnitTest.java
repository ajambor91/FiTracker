package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.Services.UserService;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class UserControllerUnitTest {
    private UserService userServiceMock;
    private UserController userController;
    private RegisterUserRequest registerUserRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {
        this.loginRequest = RequestsDataFactory.createTestLoginRequestData();
        this.registerUserRequest = RequestsDataFactory.createTestRegisterUserRequest();
        this.userServiceMock = mock(UserService.class);
        this.userController = new UserController(this.userServiceMock);
    }

    @Test
    @DisplayName("Should return RegisterSuccess response entity")
    public void testRegister() {
        when(this.userServiceMock.registerUser(eq(this.registerUserRequest))).thenReturn(UserDataTestFactory.createTestUser());
        ResponseEntity<RegisterSuccess> response = this.userController.register(this.registerUserRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return LoginSuccess response entity")
    public void testLogin() {
        when(this.userServiceMock.loginUser(eq(this.loginRequest))).thenReturn(UserDataTestFactory.createTestUser());
        ResponseEntity<LoginResponseSuccess> response = this.userController.login(this.loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return users list when find by ids")
    public void testFindUsersByIds() {
        List<User> testUsersList = new ArrayList<>(List.of(
                UserDataTestFactory.createTestUser(),
                UserDataTestFactory.createSecondTestUser()
        ));
        List<Long> testIds = new ArrayList<>(List.of(
                TEST_USER_ID,
                TEST_USER_SECOND_ID
        ));
        when(this.userServiceMock.findUsersByIds(eq(testIds))).thenReturn(testUsersList);
        ResponseEntity<FindUserResponse> response = this.userController.findUsersByIds(testIds);
        verify(this.userServiceMock, times(1)).findUsersByIds(eq(testIds));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getUserData().size());
        FindUserResponse.FindUser firstUser = response.getBody().getUserData().getFirst();
        FindUserResponse.FindUser secondUser = response.getBody().getUserData().getLast();
        assertEquals(TEST_USER_ID, firstUser.id());
        assertEquals(TEST_USER_NAME, firstUser.name());
        assertEquals(TEST_USER_EMAIL, firstUser.email());
        assertEquals(TEST_USER_SECOND_ID, secondUser.id());
        assertEquals(TEST_USER_SECOND_NAME, secondUser.name());
        assertEquals(TEST_USER_SECOND_EMAIL, secondUser.email());
    }

    @Test
    @DisplayName("Should return users list when find by email")
    public void testFindUsersByEmails() {
        List<User> testUsersList = new ArrayList<>(List.of(
                UserDataTestFactory.createTestUser(),
                UserDataTestFactory.createSecondTestUser()
        ));
        when(this.userServiceMock.findUsersByEmail(eq(TEST_USER_EMAIL))).thenReturn(testUsersList);
        ResponseEntity<FindUserResponse> response = this.userController.findUserByEmail(TEST_USER_EMAIL);
        verify(this.userServiceMock, times(1)).findUsersByEmail(eq(TEST_USER_EMAIL));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getUserData().size());
        FindUserResponse.FindUser firstUser = response.getBody().getUserData().getFirst();
        FindUserResponse.FindUser secondUser = response.getBody().getUserData().getLast();
        assertEquals(TEST_USER_ID, firstUser.id());
        assertEquals(TEST_USER_NAME, firstUser.name());
        assertEquals(TEST_USER_EMAIL, firstUser.email());
        assertEquals(TEST_USER_SECOND_ID, secondUser.id());
        assertEquals(TEST_USER_SECOND_NAME, secondUser.name());
        assertEquals(TEST_USER_SECOND_EMAIL, secondUser.email());
    }


}
