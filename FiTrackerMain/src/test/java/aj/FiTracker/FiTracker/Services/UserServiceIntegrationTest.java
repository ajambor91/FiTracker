package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static aj.FiTracker.FiTracker.TestUtils.TestData.TEST_USER_LOGIN;
import static aj.FiTracker.FiTracker.TestUtils.TestData.TEST_USER_NAME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
public class UserServiceIntegrationTest extends AbstractIntegrationTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private User user;
    private RegisterUserRequest duplicatedUser;
    private RegisterUserRequest registerUserRequest;
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
    @DisplayName("Should return User entity based on RegisterUserRequest")
    public void testRegisterUser() {
        User user = userService.registerUser(this.registerUserRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
    }

    @Test
    @DisplayName("Should throw NullPointerException when RegisterUserRequest is null")
    public void testRegisterUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.registerUser(null);
        });
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException on duplicate user")
    public void testRegisterUserThrowsDataIntegrityViolationException() {
        User user = userService.registerUser(this.duplicatedUser);
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(this.duplicatedUser);
        });
    }

    @Test
    @DisplayName("Should authenticate user")
    public void testLoginUser() {

        User user = userService.registerUser(this.registerUserRequest);
        User authenticatedUser = userService.loginUser(loginRequest);
        assertEquals(TEST_USER_LOGIN, authenticatedUser.getLogin());
        assertEquals(TEST_USER_NAME, authenticatedUser.getName());
        assertNotNull(authenticatedUser.getJwt());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when password incorrect")
    public void testLoginUserWithIncorrectPassword() {
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            User user = userService.registerUser(this.registerUserRequest);
            User authenticatedUser = userService.loginUser(registerUserRequestIncorrectPassword);

        });
        assertEquals("Incorrect password for user " + TEST_USER_LOGIN, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when user doesn't exist")
    public void testLoginUserWhenDoesntExist() {
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            User authenticatedUser = userService.loginUser(registerUserRequestIncorrectPassword);

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
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password,email, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@test.pl', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        List<User> userList = this.userService.findUsersByEmail("test");
        assertEquals(1, userList.size());
        assertEquals("Test name", userList.getFirst().getName());
        assertEquals("test@test.pl", userList.getFirst().getEmail());
    }

    @Test
    @DisplayName("Should find users by ids")
    public void testFindUsersByIds() {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password,email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (0,'testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@test.pl', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (1,'testLogin1', 'Test name', 'IncorrectPassword', " +
                        "'test2@test.pl','xAcJlQ5mjvc6QsK0AF+hkA==', 'e7058eb5-3b8e-41f7-a972-c039097d7529', NOW(), NOW())"
        );
        long id1 = 0;
        long id2 = 1;
        List<User> userList = this.userService.findUsersByIds(new ArrayList<>(List.of(id1, id2)));
        assertEquals(2, userList.size());
        assertEquals("Test name", userList.getFirst().getName());
        assertEquals("test@test.pl", userList.getFirst().getEmail());
        assertEquals("Test name", userList.getLast().getName());
        assertEquals("test2@test.pl", userList.getLast().getEmail());
    }
}
