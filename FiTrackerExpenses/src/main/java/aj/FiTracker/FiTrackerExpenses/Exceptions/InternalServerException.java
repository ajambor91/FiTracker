package aj.FiTracker.FiTrackerExpenses.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends HttpException {
    public InternalServerException(Exception exception) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
