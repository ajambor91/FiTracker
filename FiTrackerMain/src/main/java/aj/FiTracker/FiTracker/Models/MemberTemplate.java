package aj.FiTracker.FiTracker.Models;



import aj.FiTracker.FiTracker.Interfaces.KafkaTemplateInterface;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class MemberTemplate implements KafkaTemplateInterface {

    private long id;

    public MemberTemplate(UserInterface userInterface) {
        this.id = userInterface.getId();
    }
}
