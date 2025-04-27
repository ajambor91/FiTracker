package aj.FiTracker.FiTracker.Models;

import aj.FiTracker.FiTracker.DTO.REST.DeleteUserRequest;
import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateUserRequest;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserImpl implements UserInterface {

    private long id;

    private String login;

    private String name;

    private String password;

    private char[] rawPassword;

    private String jwt;

    private String salt;


    private String email;

    private UUID uniqueId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserImpl(UpdateUserRequest userRequest) {
        this.login = userRequest.getLogin();
        this.name = userRequest.getName();
        this.rawPassword = userRequest.getRawPassword();
        this.email = userRequest.getEmail();
    }

    public UserImpl(LoginRequest loginRequest) {
        this.login = loginRequest.getLogin();
        this.rawPassword = loginRequest.getRawPassword();
    }

    public UserImpl(DeleteUserRequest deleteUserRequest, long id) {
        this.rawPassword = deleteUserRequest.getRawPassword();
        this.login = deleteUserRequest.getLogin();
        this.id = id;
    }
}
