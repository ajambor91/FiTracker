package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.LoginResponseSuccess;
import aj.FiTracker.FiTracker.DTO.REST.RegisterSuccess;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.UserAlreadyExistsException;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


}
