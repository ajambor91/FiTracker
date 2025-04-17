package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.Entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;

public class UserDataTestFactory {
    public static User createTestUser() {
        User user = new User(RequestsDataFactory.createTestRegisterUserRequest());
        user.setId(TEST_USER_ID);
        return user;
    }

    public static User createSecondTestUser() {
        User user = new User();
        user.setJwt("JWT");
        user.setId(TEST_USER_SECOND_ID);
        user.setCreatedAt(LocalDateTime.now());
        user.setSalt("SALT");
        user.setEmail(TEST_USER_SECOND_EMAIL);
        user.setUniqueId(UUID.randomUUID());
        user.setLogin(TEST_USER_SECOND_LOGIN);
        user.setName(TEST_USER_SECOND_NAME);
        return user;
    }
}
