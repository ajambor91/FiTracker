package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserResponse;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserResponse extends BaseUserResponse {
    private long id;

    public UpdateUserResponse(UserInterface userInterface) {
        super(userInterface);
        this.id = userInterface.getId();
    }
}
