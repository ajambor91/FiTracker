package aj.FiTracker.FiTracker.Documents;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Getter
@Document(collection = "zones")
public class Zone {
    @Id
    private String id;
    private String name;
    private List<Member> members;

    public record  Member(long userId, String role){
    }
}
