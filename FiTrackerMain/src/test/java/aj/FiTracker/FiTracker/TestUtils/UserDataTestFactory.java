package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.Models.MemberTemplate;

import java.time.LocalDateTime;
import java.util.List;
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

    public static User createUserToUpdate() {
        User user = createTestUser();
        user.setName(TEST_USER_NAME_UPDATE);
        return user;
    }

    public static LoginResponse createLoginDTO() {
        return new LoginResponse(createTestUser());
    }

    public static RegisterUserRequestResponse createRegisterDTO() {
        return new RegisterUserRequestResponse(createTestUser());
    }

    public static FindUserResponse createFindUSersResponse() {
        return new FindUserResponse(List.of(createTestUser(), createSecondTestUser()));
    }

    public static GetUserResponse getUserResponse() {
        return new GetUserResponse(createTestUser());
    }

    public static UpdateUserResponse getUpdateRename() {
        return new UpdateUserResponse(createTestUser());
    }

    public static MemberTemplate createMemberTemplate() {return new MemberTemplate(createTestUser());}
}
