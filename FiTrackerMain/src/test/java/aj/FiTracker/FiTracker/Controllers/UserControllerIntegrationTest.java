package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.DTO.REST.DeleteUserRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateUserRequest;
import aj.FiTracker.FiTracker.Security.WithMockJwt;
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

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Tag("integration")
@ActiveProfiles("integration")
@WithMockJwt
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
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestRegisterUserRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should login user and return 200")
    public void testLoginUser() throws Exception {
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestData())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.name").value(TEST_USER_NAME));
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
        this.insertTestFirstUserWithIncorrectPassword();
        this.mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createTestLoginRequestDataWithIncorrectPassword())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return Users by email")
    public void testFindUserByEmail() throws Exception {
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(get("/users/user/find?email=test")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData[0].id").exists())
                .andExpect(jsonPath("$.userData[0].email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.userData[0].name").value(TEST_USER_NAME));
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
        this.insertTestUserIntoDataBase();
        this.insertTestUserWithIncorrectPassword();
        this.mockMvc.perform(get("/users/user/find/multi?ids=0&ids=1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userData[0].id").exists())
                .andExpect(jsonPath("$.userData[0].email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.userData[0].name").value(TEST_USER_NAME));
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

    @Test
    @DisplayName("Should return user when getting user by id")
    public void testGetUser() throws Exception {
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(get("/users/user/" + TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.name").value(TEST_USER_NAME))
                .andExpect(jsonPath("$.login").value(TEST_USER_LOGIN))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID));
    }

    @Test
    @DisplayName("Should return NotFound when getting user by id and user does not exist")
    public void testGetUserNotFound() throws Exception {
        this.mockMvc.perform(get("/users/user/" + TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return NotFound when getting user by id and user does not exist")
    public void testGetUserNot() throws Exception {
        this.mockMvc.perform(get("/users/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update user")
    public void testUpdateUser() throws Exception {
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(patch("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createUpdateUserRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "email": "test@mail.com", 
                            "login": "testLogin",
                            "name": "Test User UPDATE",
                            "userId": 1
                        }
                        """));
    }

    @Test
    @DisplayName("Should throw Unauthorized user when trying to update")
    public void testUpdateUserUnauthorized() throws Exception {
        this.insertTestUserIntoDataBase();
        UpdateUserRequest userRequest = RequestsDataFactory.createUpdateUserRequest();
        userRequest.setRawPassword("INCORECCaT7#".toCharArray());
        this.mockMvc.perform(patch("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should throw NotFound user when trying to update")
    public void testUpdateUserNotFound() throws Exception {
        this.mockMvc.perform(patch("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createUpdateUserRequest())))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() throws Exception {
        this.insertTestUserIntoDataBase();
        this.mockMvc.perform(post("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createDeleteUser())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should throw Unauthorized user when trying to delete")
    public void testDeleteUserUnauthorized() throws Exception {
        this.insertTestUserIntoDataBase();
        DeleteUserRequest deleteUserRequest = RequestsDataFactory.createDeleteUser();
        deleteUserRequest.setRawPassword("INCORECCaT7#".toCharArray());
        this.mockMvc.perform(post("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should throw NotFound user when trying to update")
    public void testDeleteUserNotFound() throws Exception {
        this.mockMvc.perform(post("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(RequestsDataFactory.createDeleteUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should throw Unauthorized user when trying to delete and set wrong password")
    public void testDeleteUserBadRequest() throws Exception {
        this.insertTestUserIntoDataBase();
        DeleteUserRequest userRequest = RequestsDataFactory.createDeleteUser();
        userRequest.setRawPassword("INCORECCT".toCharArray());
        this.mockMvc.perform(post("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized());
    }
}
