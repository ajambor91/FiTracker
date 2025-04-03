package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.Services.UserService;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@Tag("integration")
@ActiveProfiles("integration")
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final UserController userController;

    @Autowired
    public UserControllerIntegrationTest(MockMvc mockMvc, UserController userController) {
        this.mockMvc = mockMvc;
        this.userController = userController;
        this.objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void clean() {
        this.truncateTable("app_core.app_user");
    }

    @Test
    @DisplayName("Should register user")
    public void testRegisterUser() throws Exception {
        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestRegisterUserRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "login": "testLogin",
                            "name": "Test User"
                        }
                        """));
    }

    @Test
    @DisplayName("Should return Conflict when user is duplicated")
    public void testRegisterUserReturnConflict() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );          this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestRegisterUserRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should login user and return 200")
    public void testLoginUser() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "login": "testLogin",
                            "name": "Test name",
                            "message": "Login successfully"
                        }
                        """));;
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void testLoginUserWhenDoesntExist() throws Exception {
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isNotFound());;
    }
    @Test
    @DisplayName("Should 401 when password is not correct")
    public void testLoginUserIncorrectPassword() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', 'IncorrectPassword', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isUnauthorized());
    }
}
