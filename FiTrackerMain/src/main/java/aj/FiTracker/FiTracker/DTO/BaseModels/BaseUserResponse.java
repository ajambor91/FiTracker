package aj.FiTracker.FiTracker.DTO.BaseModels;

import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseUserResponse {
    private String login;
    private String name;
    private String email;
    private long userId;

    public BaseUserResponse() {
    }

    public BaseUserResponse(UserInterface user) {
        this.name = user.getName();
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.userId = user.getId();
    }

}
