package aj.FiTracker.FiTracker.DTO.BaseModels;

import aj.FiTracker.FiTracker.DTO.Users.UserData;
import aj.FiTracker.FiTracker.Entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseUser implements UserData {
    @NotBlank
    private String login;
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    public BaseUser() {
    }

    public BaseUser(User user) {
        this.name = user.getName();
        this.login = user.getLogin();
        this.email = user.getEmail();
    }

}
