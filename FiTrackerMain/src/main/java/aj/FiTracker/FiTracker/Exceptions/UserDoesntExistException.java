package aj.FiTracker.FiTracker.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserDoesntExistException extends HttpException {

    public UserDoesntExistException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
