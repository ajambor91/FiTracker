package aj.FiTracker.FiTracker.Entities;

import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequestRequest;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(
        schema = "app_core",
        name = "app_user"
)
@SequenceGenerator(name = "app_user_seq_gen", sequenceName = "app_user_id_seq", allocationSize = 1)
public class User implements UserInterface {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq_gen")
    private long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false, unique = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Transient
    private char[] rawPassword;

    @Transient
    private String jwt;

    @Column(nullable = false)
    private String salt;


    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "unique_id")
    private UUID uniqueId;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;


    public User() {
    }

    public User(RegisterUserRequestRequest userRequest) {
        this.login = userRequest.getLogin();
        this.name = userRequest.getName();
        this.rawPassword = userRequest.getRawPassword();
        this.email = userRequest.getEmail();
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.uniqueId = UUID.randomUUID();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void updateUser(UserInterface userToUpdate) {
        if (userToUpdate.getEmail() != null && !userToUpdate.getEmail().equals(this.email)) {
            this.email = userToUpdate.getEmail();
        }

        if (userToUpdate.getName() != null && !userToUpdate.getName().equals(this.name)) {
            this.name = userToUpdate.getName();
        }
    }
}
