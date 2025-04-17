package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FindUserResponse {

    private List<FindUser> userData;

    public FindUserResponse(List<User> users) {
        this.userData = getUsers(users);
    }

    private List<FindUser> getUsers(List<User> users) {
        List<FindUser> userList = new ArrayList<>();
        users.forEach(user -> {
            userList.add(new FindUser(user.getId(), user.getName(), user.getEmail()));
        });
        return userList;
    }

    public record FindUser(long id, String name, String email) {
    }
}
