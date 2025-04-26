package aj.FiTracker.FiTracker.DTO.REST;

import aj.FiTracker.FiTracker.DTO.BaseModels.BaseUserRequest;
import aj.FiTracker.FiTracker.Serializers.PasswordDeserializer;
import aj.FiTracker.FiTracker.Validators.PasswordValidatorAnnotation;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest extends BaseUserRequest {

    @JsonDeserialize(using = PasswordDeserializer.class)
    @PasswordValidatorAnnotation
    private char[] rawPassword;
}
