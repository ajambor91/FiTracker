package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.Serializers.PasswordDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequest {
    @NotNull
    private String login;
    @JsonDeserialize(using = PasswordDeserializer.class)
    private char[] rawPassword;
    public DeleteUserRequest() {
    }
}
