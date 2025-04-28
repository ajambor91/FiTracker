package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserRequest;
import aj.FiTracker.FiTracker.Serializers.PasswordDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest{
    private long userId;
    @NotBlank
    private String login;
    private String name;
    private String email;
    @JsonDeserialize(using = PasswordDeserializer.class)
    private char[] rawPassword;
    public UpdateUserRequest(){}
}
