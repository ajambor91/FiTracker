package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.UserAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.UserDoesntExistException;
import aj.FiTracker.FiTracker.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTracker.Repositories.UserRepository;
import aj.FiTracker.FiTracker.Security.JWTService;
import aj.FiTracker.FiTracker.Security.PasswordEncoder;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import aj.FiTracker.FiTracker.UserInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class UserServiceUnitTest {

    private JWTService jwtService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private User user;
    private RegisterUserRequestRequest registerUserRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {

        this.passwordEncoder = new PasswordEncoder();
        this.jwtService = mock(JWTService.class);
        this.userRepository = mock(UserRepository.class);
        this.registerUserRequest = RequestsDataFactory.createTestRegisterUserRequest();
        this.user = UserDataTestFactory.createTestUser();
        this.loginRequest = RequestsDataFactory.createTestLoginRequestData();
        this.userService = new UserService(passwordEncoder, userRepository, jwtService);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException for duplicates")
    public void testRegisterUserThrowsUserAlreadyExistsException() {
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAlreadyExistsException.class, () -> {
            this.userService.registerUser(this.registerUserRequest);
        });
        verify(this.userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("Should throw InternalServerException for unknown exceptions")
    public void testRegisterUserThrowsDInternalServerException() {
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.registerUser(this.registerUserRequest);
        });
        verify(this.userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("Should return User entity based on RegisterUserRequestRequest")
    public void testRegisterUser() {
        when(this.userRepository.saveAndFlush(any(User.class))).thenReturn(this.user);
        RegisterUserRequestResponse user = userService.registerUser(this.registerUserRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
        verify(this.userRepository, times(1)).saveAndFlush(any(User.class));

    }

    @Test
    @DisplayName("Should return User entity based on LoginRequest")
    public void testLoginUser() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(true);
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.of(this.user));
        this.user.setJwt("TEST");
        when(this.jwtService.generateToken(any(User.class))).thenReturn(this.user);
        LoginResponse user = userService.loginUser(this.loginRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
        assertEquals("TEST", user.getJwt());
        verify(this.userRepository, times(1)).findOneByLogin(any(String.class));
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when checking password returns false")
    public void testLoginUserIncorrectPassword() {
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(false);
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.of(this.user));
        assertThrows(UserUnauthorizedException.class, () -> {
            LoginResponse user = userService.loginUser(this.loginRequest);
        });
        verify(this.userRepository, times(1)).findOneByLogin(any(String.class));
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when cannot find user")
    public void testLoginUserUserDoesntExist() {
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.ofNullable(null));
        assertThrows(UserDoesntExistException.class, () -> {
            LoginResponse user = userService.loginUser(this.loginRequest);

        });
        verify(this.userRepository, times(1)).findOneByLogin(any(String.class));

    }

    @Test
    @DisplayName("Should throw NullPointerException when LoginRequest is null")
    public void testloginUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.loginUser(null);
        });
        verify(this.userRepository, never()).findOneByLogin(any(String.class));

    }

    @Test
    @DisplayName("Should throw NullPointerException when RegisterUserRequestRequest is null")
    public void testRegisterUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.registerUser(null);
        });
        verify(this.userRepository, never()).saveAndFlush(any(User.class));

    }

    @Test
    @DisplayName("Should throw InternalServerException for unknown exceptions when user tries login")
    public void testLoginUserThrowsDInternalServerException() {
        when(this.userRepository.findOneByLogin(any(String.class))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.loginUser(this.loginRequest);
        });
        verify(this.userRepository, times(1)).findOneByLogin(any(String.class));
    }

    @Test
    @DisplayName("Should return found users by email")
    public void testFindUsersByEmail() {
        String emailsRegExp = "^" + TEST_USER_EMAIL;
        List<User> testUsers = new ArrayList<>(List.of(UserDataTestFactory.createTestUser(), UserDataTestFactory.createSecondTestUser()));
        when(this.userRepository.findUsersByEmail(eq(emailsRegExp))).thenReturn(testUsers);
        FindUserResponse users = this.userService.findUsersByEmail(TEST_USER_EMAIL);
        verify(this.userRepository, times(1)).findUsersByEmail(eq(emailsRegExp));
        assertEquals(2, users.getUserData().size());
        FindUserResponse.FindUser firstUser = users.getUserData().getFirst();
        FindUserResponse.FindUser secondUser = users.getUserData().getLast();
        assertEquals(TEST_USER_ID, firstUser.id());
        assertEquals(TEST_USER_NAME, firstUser.name());
        assertEquals(TEST_USER_EMAIL, firstUser.email());
        assertEquals(TEST_USER_SECOND_ID, secondUser.id());
        assertEquals(TEST_USER_SECOND_NAME, secondUser.name());
        assertEquals(TEST_USER_SECOND_EMAIL, secondUser.email());
    }

    @Test
    @DisplayName("Should return empty array when users not found")
    public void testFindUsersByEmailShouldReturnEmptyList() {
        List<User> testUsers = new ArrayList<>();
        String emailsRegExp = "^" + TEST_USER_EMAIL;

        when(this.userRepository.findUsersByEmail(eq(emailsRegExp))).thenReturn(testUsers);
        FindUserResponse users = this.userService.findUsersByEmail(TEST_USER_EMAIL);
        verify(this.userRepository, times(1)).findUsersByEmail(eq(emailsRegExp));
        assertEquals(0, users.getUserData().size());
    }

    @Test
    @DisplayName("Should return InternalServerException on any exception")
    public void testFindUsersByEmailInternalServerException() {
        String emailsRegExp = "^" + TEST_USER_EMAIL;

        when(this.userRepository.findUsersByEmail(eq(emailsRegExp))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.findUsersByEmail(TEST_USER_EMAIL);
        });
        verify(this.userRepository, times(1)).findUsersByEmail(eq(emailsRegExp));

    }

    @Test
    @DisplayName("Should return found users by ids")
    public void testFindUsersByIds() {
        List<Long> ids = new ArrayList<>(List.of(TEST_USER_ID, TEST_USER_SECOND_ID));
        List<User> testUsers = new ArrayList<>(List.of(UserDataTestFactory.createTestUser(), UserDataTestFactory.createSecondTestUser()));
        when(this.userRepository.findUsersByIds(eq(ids))).thenReturn(testUsers);
        FindUserResponse users = this.userService.findUsersByIds(ids);
        verify(this.userRepository, times(1)).findUsersByIds(eq(ids));
        assertEquals(2, users.getUserData().size());
        FindUserResponse.FindUser firstUser = users.getUserData().getFirst();
        FindUserResponse.FindUser secondUser = users.getUserData().getLast();
        assertEquals(TEST_USER_NAME, firstUser.name());
        assertEquals(TEST_USER_EMAIL, firstUser.email());
        assertEquals(TEST_USER_SECOND_NAME, secondUser.name());
        assertEquals(TEST_USER_SECOND_EMAIL, secondUser.email());
    }

    @Test
    @DisplayName("Should return empty array when users by ids not found")
    public void testFindUsersByIdsShouldReturnEmptyList() {
        List<User> testUsers = new ArrayList<>();
        List<Long> ids = new ArrayList<>(List.of(TEST_USER_ID, TEST_USER_SECOND_ID));

        when(this.userRepository.findUsersByIds(eq(ids))).thenReturn(testUsers);
        FindUserResponse users = this.userService.findUsersByIds(ids);
        verify(this.userRepository, times(1)).findUsersByIds(eq(ids));
        assertEquals(0, users.getUserData().size());
    }

    @Test
    @DisplayName("Should return InternalServerException on any exception when try find users by ids")
    public void testFindUsersByIdsInternalServerException() {
        List<Long> ids = new ArrayList<>(List.of(TEST_USER_ID, TEST_USER_SECOND_ID));

        List<User> testUsers = new ArrayList<>();
        when(this.userRepository.findUsersByIds(eq(ids))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.findUsersByIds(ids);
        });
        verify(this.userRepository, times(1)).findUsersByIds(eq(ids));
    }

    @Test
    @DisplayName("Should get user by id")
    public void testGetUser() {

        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(UserDataTestFactory.createTestUser()));
        GetUserResponse userRequestResponse = this.userService.getUser(TEST_USER_ID);
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertEquals(TEST_USER_ID, userRequestResponse.getUserId());
        assertEquals(TEST_USER_NAME, userRequestResponse.getName());
        assertEquals(TEST_USER_EMAIL, userRequestResponse.getEmail());
        assertEquals(TEST_USER_LOGIN, userRequestResponse.getLogin());
    }

    @Test
    @DisplayName("Should return UserDoesntExistException when user cannot be found")
    public void testGetUserUserDoesntExistException() {
        when(this.userRepository.findOneById(anyLong())).thenThrow(new UserDoesntExistException("User cannot be found"));
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
           this.userService.getUser(TEST_USER_ID);
        });
        verify(this.userRepository, times(1)).findOneById(anyLong());
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("User cannot be found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return UserDoesntExistException when user cannot be found")
    public void testGetUserInternalServerException() {
        when(this.userRepository.findOneById(anyLong())).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.userService.getUser(TEST_USER_ID);
        });
        verify(this.userRepository, times(1)).findOneById(anyLong());
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    @Test
    @DisplayName("Should update user")
    public void testUpdateUser() {
        Authentication authentication = this.createAuthMock();
        User user = UserDataTestFactory.createTestUser();
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(true);
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(user));
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        UpdateUserResponse updateUserResponse = this.userService.updateUser(userRequest, authentication);
        verify(this.userRepository,times(1)).save(any(User.class));
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertEquals(TEST_USER_LOGIN, updateUserResponse.getLogin());
        assertEquals(TEST_USER_EMAIL, updateUserResponse.getEmail());
        assertEquals(TEST_USER_ID, updateUserResponse.getUserId());
        assertEquals(TEST_USER_NAME_UPDATE, updateUserResponse.getName());
    }

    @Test
    @DisplayName("Should throw when InternalServerException 'cannot get id from token' when updating user")
    public void testUpdateUserUserUnauthorizedException() {
        Authentication authentication = this.createAuthMockWithNullId();
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.userService.updateUser(userRequest, authentication);
        });
        verify(this.userRepository,never()).save(any(User.class));
        verify(this.userRepository, never()).findOneById(anyLong());
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Cannot parse null string", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw when UserDoesntExistException 'cannot find user {id}' when updating user")
    public void testUpdateUserUserDoesntExistException() {
        Authentication authentication = this.createAuthMock();
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            this.userService.updateUser(userRequest, authentication);
        });
        verify(this.userRepository,never()).save(any(User.class));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("Cannot find user " + TEST_USER_ID, exception.getMessage());
    }


    @Test
    @DisplayName("Should throw when UserUnauthorizedException 'Incorrect password' when updating user")
    public void testUpdateUserUserUnauthorizedExceptionIncorrectPassword() {
        Authentication authentication = this.createAuthMock();
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(user));
        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.userService.updateUser(userRequest, authentication);
        });
        verify(this.userRepository,never()).save(any(User.class));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(UserUnauthorizedException.class, exception);
        assertEquals("Incorrect password for user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw when InternalServerException on error while getting user from database")
    public void testUpdateUserInternalServerException() {
        Authentication authentication = this.createAuthMock();
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.userService.updateUser(userRequest, authentication);
        });
        verify(this.userRepository,never()).save(any(User.class));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw when InternalServerException on error while updating user record")
    public void testUpdateUserInternalServerExceptionWhileUpdatingRecord() {
        Authentication authentication = this.createAuthMock();
        User user = UserDataTestFactory.createTestUser();
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(true);
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(user));
        when(this.userRepository.save(any(User.class))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.userService.updateUser(userRequest, authentication);
        });
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
        verify(this.userRepository,times(1)).save(any(User.class));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() {
        User user = UserDataTestFactory.createTestUser();

        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        Authentication authentication = this.createAuthMock();
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(true);
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(user));

        this.userService.deleteUser(deleteUserRequest, authentication);
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
        verify(this.userRepository,times(1)).deleteById(eq(TEST_USER_ID));
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
    }

    @Test
    @DisplayName("Should when UserUnauthorizedException when deleting user")
    public void testDeleteUserUserUnauthorizedException() {
        User user = UserDataTestFactory.createTestUser();
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        Authentication authentication = this.createAuthMock();
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(UserInterface.class), any(UserInterface.class))).thenReturn(false);
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.of(user));
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.userService.deleteUser(deleteUserRequest, authentication);
        });
        verify(passwordEncoderMock, times(1)).checkPass(any(UserInterface.class), any(UserInterface.class));
        verify(this.userRepository,never()).deleteById(anyLong());
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(UserUnauthorizedException.class, userUnauthorizedException);
        assertEquals("Incorrect password for user " + TEST_USER_ID, userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when deleting user")
    public void testDeleteUserUserDoesntExistException() {
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        Authentication authentication = this.createAuthMock();
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenReturn(Optional.empty());
        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class, () -> {
            this.userService.deleteUser(deleteUserRequest, authentication);
        });
        verify(this.userRepository,never()).deleteById(anyLong());
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(UserDoesntExistException.class, exception);
        assertEquals("Cannot find user " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InternalServerException when deleting user")
    public void testDeleteUserInternalServerException() {
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        Authentication authentication = this.createAuthMock();
        when(this.userRepository.findOneById(eq(TEST_USER_ID))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.userService.deleteUser(deleteUserRequest, authentication);
        });
        verify(this.userRepository,never()).deleteById(anyLong());
        verify(this.userRepository, times(1)).findOneById(eq(TEST_USER_ID));
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    private Authentication createAuthMock() {
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(TEST_USER_ID));
        when(jwtMock.getClaimAsString(eq("name"))).thenReturn(TEST_USER_EMAIL);
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        return authenticationMock;
    }
    private Authentication createAuthMockWithNullId() {
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaimAsString(eq("sub"))).thenReturn(null);
        when(jwtMock.getClaimAsString(eq("name"))).thenReturn(TEST_USER_EMAIL);
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(jwtMock);
        return authenticationMock;
    }


}
