package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.Entities.User;

public class UserDataTestFactory {
    public static User createTestUser() {
        User user = new User(RequestsDataFactory.createTestRegisterUserRequest());
        return user;
    }
}
