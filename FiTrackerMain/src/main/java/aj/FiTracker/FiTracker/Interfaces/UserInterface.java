package aj.FiTracker.FiTracker.Interfaces;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserInterface {
    long getId();
    void setId(long id);

    String getLogin();
    void setLogin(String login);

    String getName();
    void setName(String name);

    String getPassword();
    void setPassword(String password);

    String getSalt();
    void setSalt(String salt);

    String getEmail();
    void setEmail(String email);

    UUID getUniqueId();
    void setUniqueId(UUID uniqueId);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    void setRawPassword(char[] password);
    char[] getRawPassword();
}
