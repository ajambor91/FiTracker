package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.Exceptions.UserAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.UserDoesntExistException;
import aj.FiTracker.FiTracker.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTracker.Repositories.UserRepository;
import aj.FiTracker.FiTracker.Security.PasswordEncoder;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
public class UserServiceIntegrationTest extends AbstractIntegrationTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private User user;
    private RegisterUserRequestRequest duplicatedUser;
    private RegisterUserRequestRequest registerUserRequest;
    private LoginRequest registerUserRequestIncorrectPassword;
    private LoginRequest loginRequest;

    @Autowired
    public UserServiceIntegrationTest(UserService userService) {
        this.userService = userService;

    }

    @AfterEach
    public void clean() {
        this.truncateTable("app_core.app_user");
    }

    @BeforeEach
    public void setup() {
        this.duplicatedUser = RequestsDataFactory.createTestDuplicatedRegisterUserRequest();
        this.registerUserRequest = RequestsDataFactory.createTestRegisterUserRequest();
        this.user = UserDataTestFactory.createTestUser();
        this.loginRequest = RequestsDataFactory.createTestLoginRequestData();
        this.registerUserRequestIncorrectPassword = RequestsDataFactory.createTestLoginRequestDataWithIncorrectPassword();
    }

    @Test
    @DisplayName("Should return User entity based on RegisterUserRequestRequest")
    public void testRegisterUser() {
        RegisterUserRequestResponse user = userService.registerUser(this.registerUserRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
    }

    @Test
    @DisplayName("Should throw NullPointerException when RegisterUserRequestRequest is null")
    public void testRegisterUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.registerUser(null);
        });
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException on duplicate user")
    public void testRegisterUserThrowsDataIntegrityViolationException() {
        RegisterUserRequestResponse user = userService.registerUser(this.duplicatedUser);
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(this.duplicatedUser);
        });
    }

    @Test
    @DisplayName("Should authenticate user")
    public void testLoginUser() {

        RegisterUserRequestResponse user = userService.registerUser(this.registerUserRequest);
        LoginResponse authenticatedUser = userService.loginUser(loginRequest);
        assertEquals(TEST_USER_LOGIN, authenticatedUser.getLogin());
        assertEquals(TEST_USER_NAME, authenticatedUser.getName());
        assertNotNull(authenticatedUser.getJwt());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when password incorrect")
    public void testLoginUserWithIncorrectPassword() {
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            RegisterUserRequestResponse user = userService.registerUser(this.registerUserRequest);
            LoginResponse authenticatedUser = userService.loginUser(registerUserRequestIncorrectPassword);

        });
        assertEquals("Incorrect password for user " + TEST_USER_LOGIN, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when user doesn't exist")
    public void testLoginUserWhenDoesntExist() {
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            LoginResponse authenticatedUser = userService.loginUser(registerUserRequestIncorrectPassword);

        });
        assertEquals("User with login " + TEST_USER_LOGIN + " does not exist.", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw NullPointerException when LoginRequest is null")
    public void testloginUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.loginUser(null);
        });
    }

    @Test
    @DisplayName("Should find users by email")
    public void testFindUsersByEmail() {
        this.insertTestUserIntoDataBase();

        FindUserResponse userList = this.userService.findUsersByEmail("test");
        assertEquals(1, userList.getUserData().size());
        assertEquals(TEST_USER_NAME, userList.getUserData().getFirst().name());
        assertEquals(TEST_USER_EMAIL, userList.getUserData().getFirst().email());
    }

    @Test
    @DisplayName("Should find users by ids")
    public void testFindUsersByIds() {
        this.insertTestUserIntoDataBase();
        this.insertTestUserWithIncorrectPassword();
        FindUserResponse userList = this.userService.findUsersByIds(new ArrayList<>(List.of(TEST_USER_ID, TEST_USER_SECOND_ID)));
        assertEquals(2, userList.getUserData().size());
        assertEquals(TEST_USER_NAME, userList.getUserData().getFirst().name());
        assertEquals(TEST_USER_EMAIL, userList.getUserData().getFirst().email());
        assertEquals(TEST_USER_SECOND_NAME, userList.getUserData().getLast().name());
        assertEquals(TEST_USER_SECOND_EMAIL, userList.getUserData().getLast().email());
    }

    @Test
    @DisplayName("Should return user from id")
    public void testGetUser() {
        this.insertTestUserIntoDataBase();
        GetUserResponse userRequestResponse = this.userService.getUser(TEST_USER_ID);
        assertEquals(TEST_USER_ID, userRequestResponse.getUserId());
        assertEquals(TEST_USER_NAME, userRequestResponse.getName());
        assertEquals(TEST_USER_EMAIL, userRequestResponse.getEmail());
        assertEquals(TEST_USER_LOGIN, userRequestResponse.getLogin());

    }

    @Test
    @DisplayName("Should throw UserDoesntExistException")
    public void testGetUserUserDoesntExistException() {
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            this.userService.getUser(TEST_USER_ID);
        }) ;
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("Cannot find user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should update user")
    public void testUpdateUser() {
        this.insertTestUserIntoDataBase();
        Authentication authenticationMock = this.createAuthMock();
        UpdateUserResponse updateUserResponse = this.userService.updateUser(RequestsDataFactory.createUpdateUserRequest(), authenticationMock);
        assertEquals(TEST_USER_EMAIL, updateUserResponse.getEmail());
        assertEquals(TEST_USER_NAME_UPDATE, updateUserResponse.getName());
        assertEquals(TEST_USER_LOGIN, updateUserResponse.getLogin());
        assertEquals(TEST_USER_ID, updateUserResponse.getUserId());
    }


    @Test
    @DisplayName("Should throw UserDoesntExistException when update user")
    public void testUpdateUserUserDoesntExistException() {

        Authentication authenticationMock = this.createAuthMock();
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            this.userService.updateUser(RequestsDataFactory.createUpdateUserRequest(), authenticationMock);
        });
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("Cannot find user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when update user")
    public void testUpdateUserUserUnauthorizedException() {
        this.insertTestUserIntoDataBase();
        Authentication authenticationMock = this.createAuthMock();
        UpdateUserRequest updateUserRequest = RequestsDataFactory.createUpdateUserRequest();
        updateUserRequest.setRawPassword("INCORRECT".toCharArray());
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.userService.updateUser(updateUserRequest, authenticationMock);
        });
        assertInstanceOf(UserUnauthorizedException.class, exception);
        assertEquals("Incorrect password for user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() {
        this.insertTestUserIntoDataBase();
        Authentication authenticationMock = this.createAuthMock();
        this.userService.deleteUser(RequestsDataFactory.createDeleteUser(), authenticationMock);
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when deleting user")
    public void testDeleteUserUserDoesntExistException() {

        Authentication authenticationMock = this.createAuthMock();
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            this.userService.deleteUser(RequestsDataFactory.createDeleteUser(), authenticationMock);
        });
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("Cannot find user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when deleting user")
    public void testDeleteUserUserUnauthorizedException() {
        this.insertTestUserIntoDataBase();
        Authentication authenticationMock = this.createAuthMock();
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        deleteUserRequest.setRawPassword("INCORRECT".toCharArray());
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.userService.deleteUser(deleteUserRequest, authenticationMock);
        });
        assertInstanceOf(UserUnauthorizedException.class, exception);
        assertEquals("Incorrect password for user " + TEST_USER_ID, exception.getMessage());
    }

    private Authentication createAuthMock() {
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(TEST_USER_ID));
        when(jwtMock.getClaimAsString(eq("name"))).thenReturn(TEST_USER_EMAIL);
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        return authenticationMock;
    }


}
