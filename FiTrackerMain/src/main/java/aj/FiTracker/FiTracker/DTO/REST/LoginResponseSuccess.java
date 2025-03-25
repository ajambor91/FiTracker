package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseSuccess {
    private String message;
    private String login;
    private String name;

    public LoginResponseSuccess(User user, String message) {
        this.login = user.getLogin();
        this.name = user.getName();
        this.message = message;
    }
}
