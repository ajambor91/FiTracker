package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Tag;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

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
    private RegisterUserRequest registerUserRequest;
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
    }

    @Test
    @DisplayName("Should throw InternalServerException for unknown exceptions")
    public void testRegisterUserThrowsDInternalServerException() {
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.registerUser(this.registerUserRequest);
        });
    }

    @Test
    @DisplayName("Should return User entity based on RegisterUserRequest")
    public void testRegisterUser() {
        when(this.userRepository.saveAndFlush(any(User.class))).thenReturn(this.user);
        User user = userService.registerUser(this.registerUserRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
    }

    @Test
    @DisplayName("Should return User entity based on LoginRequest")
    public void testLoginUser() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(User.class), any(User.class))).thenReturn(true);
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.of(this.user));
        this.user.setJwt("TEST");
        when(this.jwtService.generateToken(any(User.class))).thenReturn(this.user);
        User user = userService.loginUser(this.loginRequest);
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_LOGIN, user.getLogin());
        assertEquals("TEST", user.getJwt());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when checking password returns false")
    public void testLoginUserIncorrectPassword() {
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(this.userService, "passwordEncoder", passwordEncoderMock);
        when(passwordEncoderMock.checkPass(any(User.class), any(User.class))).thenReturn(false);
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.of(this.user));
        assertThrows(UserUnauthorizedException.class, () -> {
            User user = userService.loginUser(this.loginRequest);

        });
    }

    @Test
    @DisplayName("Should throw UserDoesntExistException when cannot find user")
    public void testLoginUserUserDoesntExist() {
        when(this.userRepository.findOneByLogin(any(String.class))).thenReturn(Optional.ofNullable(null));
        assertThrows(UserDoesntExistException.class, () -> {
            User user = userService.loginUser(this.loginRequest);

        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when LoginRequest is null")
    public void testloginUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.loginUser(null);
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when RegisterUserRequest is null")
    public void testRegisterUserThrowsNullPointerExceptcion() {
        assertThrows(NullPointerException.class, () -> {
            userService.registerUser(null);
        });
    }

    @Test
    @DisplayName("Should throw InternalServerException for unknown exceptions when user tries login")
    public void testLoginUserThrowsDInternalServerException() {
        when(this.userRepository.findOneByLogin(any(String.class))).thenThrow(RuntimeException.class);
        assertThrows(InternalServerException.class, () -> {
            this.userService.loginUser(this.loginRequest);
        });
    }
}
