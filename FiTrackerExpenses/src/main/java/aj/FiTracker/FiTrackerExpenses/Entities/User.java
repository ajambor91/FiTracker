package aj.FiTracker.FiTrackerExpenses.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(
        schema = "app_data",
        name = "app_user"
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    @Column(nullable = false, name = "app_user_id")
    private long userId;

    @Column(nullable = false, name = "app_zone_id")
    private String zoneId;

    public User(long userId, String zoneId) {
        this.userId = userId;
        this.zoneId = zoneId;
    }

    public User() {
    }
}
