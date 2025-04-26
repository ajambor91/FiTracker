package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequestRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequestResponse;
import aj.FiTracker.FiTracker.DTO.REST.UpdateUserRequest;

import static aj.FiTracker.FiTracker.TestUtils.TestData.*;


public class RequestsDataFactory {


    public static RegisterUserRequestRequest createTestRegisterUserRequest() {
        RegisterUserRequestRequest request = new RegisterUserRequestRequest();
        request.setName(TEST_USER_NAME);
        request.setEmail(TEST_USER_EMAIL);
        request.setLogin(TEST_USER_LOGIN);
        request.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());

        return request;
    }

    public static RegisterUserRequestRequest createTestDuplicatedRegisterUserRequest() {
        RegisterUserRequestRequest request = new RegisterUserRequestRequest();
        request.setName(TEST_USER_NAME);
        request.setEmail(TEST_USER_EMAIL);
        request.setLogin(TEST_DUPLICATED_USER_LOGIN);
        request.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());

        return request;
    }

    public static RegisterUserRequestResponse createTestRegisterSuccessResponse() {
        return new RegisterUserRequestResponse(UserDataTestFactory.createTestUser());
    }

    public static LoginRequest createTestLoginRequestData() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(TEST_USER_LOGIN);
        loginRequest.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());
        return loginRequest;

    }

    public static LoginRequest createTestLoginRequestDataWithIncorrectPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(TEST_USER_LOGIN);
        loginRequest.setRawPassword(TEST_INCORRECT_PASSWORD.toCharArray());
        return loginRequest;

    }

    public static UpdateUserRequest createUpdateUserRequest() {
        UpdateUserRequest userRequest = new UpdateUserRequest();
        userRequest.setLogin(TEST_USER_LOGIN);
        userRequest.setName(TEST_USER_NAME_UPDATE);
        userRequest.setEmail(TEST_USER_EMAIL);
        userRequest.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());
        return userRequest;
    }
}
