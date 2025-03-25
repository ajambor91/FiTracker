package aj.FiTracker.FiTracker.TestUtils;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterSuccess;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
import static aj.FiTracker.FiTracker.TestUtils.TestData.*;


public class RequestsDataFactory {


    public static RegisterUserRequest createTestRegisterUserRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName(TEST_USER_NAME);
        request.setLogin(TEST_USER_LOGIN);
        request.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());

        return request;
    }

    public static RegisterUserRequest createTestDuplicatedRegisterUserRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName(TEST_USER_NAME);
        request.setLogin(TEST_DUPLICATED_USER_LOGIN);
        request.setRawPassword(TEST_CORRECT_PASSWORD.toCharArray());

        return request;
    }

    public static RegisterSuccess createTestRegisterSuccessResponse() {
        return new RegisterSuccess(UserDataTestFactory.createTestUser());
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
}
