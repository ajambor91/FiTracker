package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.TestUtils.RequestsDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                "INSERT INTO app_core.app_user (login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@test.pl'," +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestRegisterUserRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should login user and return 200")
    public void testLoginUser() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password,email, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@test.pl', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "login": "testLogin",
                            "name": "Test name",
                            "message": "Login successfully"
                        }
                        """));
        ;
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void testLoginUserWhenDoesntExist() throws Exception {
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isNotFound());
        ;
    }

    @Test
    @DisplayName("Should 401 when password is not correct")
    public void testLoginUserIncorrectPassword() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES ('testLogin', 'Test name', 'IncorrectPassword', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', 'test@test.pl', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return Users by email")
    public void testFindUserByEmail() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (0,'testLogin', 'Test name', 'IncorrectPassword', " +
                        "'test@test.pl','xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.mockMvc.perform(get("/users/user/find?email=test")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData[0].id").exists())
                .andExpect(jsonPath("$.userData[0].email").value("test@test.pl"))
                .andExpect(jsonPath("$.userData[0].name").value("Test name"));
    }

    @Test
    @DisplayName("Should return empty list when user does not found")
    public void testFindUserByEmailEmptyList() throws Exception {
        this.mockMvc.perform(get("/users/user/find?email=test")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData").isEmpty());
    }

    @Test
    @DisplayName("Should return BadRequest when email is null when trying find user by email")
    public void testFindUserByEmailBadRequestWhenEmailIsBlank() throws Exception {
        this.mockMvc.perform(get("/users/user/find?email=")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return Users by ids")
    public void testFindUserByIds() throws Exception {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (0,'testLogin', 'Test name', 'IncorrectPassword', " +
                        "'test@test.pl','xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (1,'testLogin1', 'Test name', 'IncorrectPassword', " +
                        "'test2@test.pl','xAcJlQ5mjvc6QsK0AF+hkA==', 'e7058eb5-3b8e-41f7-a972-c039097d7529', NOW(), NOW())"
        );
        this.mockMvc.perform(get("/users/user/find/multi?ids=0&ids=1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData[0].id").exists())
                .andExpect(jsonPath("$.userData[0].email").value("test@test.pl"))
                .andExpect(jsonPath("$.userData[0].name").value("Test name"));
    }

    @Test
    @DisplayName("Should return Users by ids and return empty list")
    public void testFindUserByIdsReturnEmptyList() throws Exception {
        this.mockMvc.perform(get("/users/user/find/multi?ids=0")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData").isEmpty());
    }

    @Test
    @DisplayName("Should return BadRequest when ids is null in find user by ids")
    public void testFindUserByIdsBadRequestIdsIsEmpty() throws Exception {
        this.mockMvc.perform(get("/users/user/find/multi?ids=")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return BadRequest when ids is not long type in find user by ids")
    public void testFindUserByIdsBadRequestIdsIsNotLong() throws Exception {
        this.mockMvc.perform(get("/users/user/find/multi?ids=invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }


}
