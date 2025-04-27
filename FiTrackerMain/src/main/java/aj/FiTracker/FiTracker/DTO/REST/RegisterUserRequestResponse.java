package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserResponse;
import aj.FiTracker.FiTracker.Entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequestResponse extends BaseUserResponse {
    private long id;

    public RegisterUserRequestResponse(User user) {
        super(user);
        this.id = user.getId();
    }
}
