package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserResponse;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserResponse extends BaseUserResponse {

    public GetUserResponse(UserInterface userInterface) {
        super(userInterface);
    }
}
