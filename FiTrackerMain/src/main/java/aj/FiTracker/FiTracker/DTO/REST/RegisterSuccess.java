package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUser;
import aj.FiTracker.FiTracker.DTO.Users.UserData;
import aj.FiTracker.FiTracker.Entities.User;

public class RegisterSuccess extends BaseUser {

    public RegisterSuccess(User user) {
        super(user);
    }
}
