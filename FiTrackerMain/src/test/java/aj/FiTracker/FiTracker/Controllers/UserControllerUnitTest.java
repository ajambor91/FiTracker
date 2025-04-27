package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Services.UserService;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
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

import java.util.ArrayList;
import java.util.List;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class UserControllerUnitTest {
    private UserService userServiceMock;
    private UserController userController;
    private RegisterUserRequestRequest registerUserRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {
        this.loginRequest = RequestsDataFactory.createTestLoginRequestData();
        this.registerUserRequest = RequestsDataFactory.createTestRegisterUserRequest();
        this.userServiceMock = mock(UserService.class);
        this.userController = new UserController(this.userServiceMock);
    }

    @Test
    @DisplayName("Should return RegisterUserRequestResponse response entity")
    public void testRegister() {
        when(this.userServiceMock.registerUser(eq(this.registerUserRequest))).thenReturn(UserDataTestFactory.createRegisterDTO());
        ResponseEntity<RegisterUserRequestResponse> response = this.userController.register(this.registerUserRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return LoginSuccess response entity")
    public void testLogin() {
        when(this.userServiceMock.loginUser(eq(this.loginRequest))).thenReturn(UserDataTestFactory.createLoginDTO());
        ResponseEntity<LoginResponse> response = this.userController.login(this.loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return users list when find by ids")
    public void testFindUsersByIds() {
        List<UserInterface> testUsersList = new ArrayList<>(List.of(
                UserDataTestFactory.createTestUser(),
                UserDataTestFactory.createSecondTestUser()
        ));
        List<Long> testIds = new ArrayList<>(List.of(
                TEST_USER_ID,
                TEST_USER_SECOND_ID
        ));
        when(this.userServiceMock.findUsersByIds(eq(testIds))).thenReturn(UserDataTestFactory.createFindUSersResponse());
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
        when(this.userServiceMock.findUsersByEmail(eq(TEST_USER_EMAIL))).thenReturn(UserDataTestFactory.createFindUSersResponse());
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

    @Test
    @DisplayName("Should get user by id")
    public void testGetUser() {
        when(this.userServiceMock.getUser(eq(TEST_USER_ID))).thenReturn(UserDataTestFactory.getUserResponse());
        ResponseEntity<GetUserResponse> getUserResponse = this.userController.getUser(TEST_USER_ID);
        verify(this.userServiceMock, times(1)).getUser(eq(TEST_USER_ID));
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        GetUserResponse getUser = getUserResponse.getBody();
        assertInstanceOf(GetUserResponse.class, getUser);
        assertEquals(TEST_USER_LOGIN, getUser.getLogin());
        assertEquals(TEST_USER_NAME, getUser.getName());
        assertEquals(TEST_USER_ID, getUser.getUserId());
        assertEquals(TEST_USER_EMAIL, getUser.getEmail());
    }

    @Test
    @DisplayName("Should update user")
    public void testUpdateUser() {
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        when(this.userServiceMock.updateUser(eq(userRequest), any(Authentication.class))).thenReturn(UserDataTestFactory.getUpdateRename());
        ResponseEntity<UpdateUserResponse> getUserResponse = this.userController.updateUser(userRequest, mock(Authentication.class));
        verify(this.userServiceMock, times(1)).updateUser(eq(userRequest), any(Authentication.class));
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        UpdateUserResponse getUser = getUserResponse.getBody();
        assertInstanceOf(UpdateUserResponse.class, getUser);
        assertEquals(TEST_USER_LOGIN, getUser.getLogin());
        assertEquals(TEST_USER_NAME, getUser.getName());
        assertEquals(TEST_USER_ID, getUser.getUserId());
        assertEquals(TEST_USER_EMAIL, getUser.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() {
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        this.userController.deleteUser(deleteUserRequest, mock(Authentication.class));
        verify(this.userServiceMock, times(1)).deleteUser(eq(deleteUserRequest), any(Authentication.class));
    }


}
