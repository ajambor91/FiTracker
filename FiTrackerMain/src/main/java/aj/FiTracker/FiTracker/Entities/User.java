package aj.FiTracker.FiTracker.Entities;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
import aj.FiTracker.FiTracker.DTO.Users.UserData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        schema = "app_core",
        name = "app_user"
)
@SequenceGenerator(name = "app_user_seq_gen", sequenceName = "app_user_id_seq", allocationSize = 1)
public class User implements UserData {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq_gen")
    private long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column( nullable = false, unique = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Transient
    private char[] rawPassword;

    @Transient
    private String jwt;

    @Column(nullable = false)
    private String salt;

    public User() {}

    public User(RegisterUserRequest userRequest) {
        this.login = userRequest.getLogin();
        this.name = userRequest.getName();
        this.rawPassword = userRequest.getRawPassword();
    }

    public User(LoginRequest loginRequest) {
        this.login = loginRequest.getLogin();
        this.rawPassword = loginRequest.getRawPassword();
    }
}
