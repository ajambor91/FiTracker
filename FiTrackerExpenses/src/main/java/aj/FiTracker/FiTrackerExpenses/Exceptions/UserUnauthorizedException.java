package aj.FiTracker.FiTrackerExpenses.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserUnauthorizedException extends HttpException{

    public UserUnauthorizedException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
