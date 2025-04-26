package aj.FiTracker.FiTracker.DTO.BaseModels;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseUserRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    public BaseUserRequest() {
    }

}
