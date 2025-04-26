package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserResponse;
import aj.FiTracker.FiTracker.Entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse extends BaseUserResponse {
    private String jwt;

    public LoginResponse(User user) {
        super(user);
        this.jwt = user.getJwt();
    }
}
