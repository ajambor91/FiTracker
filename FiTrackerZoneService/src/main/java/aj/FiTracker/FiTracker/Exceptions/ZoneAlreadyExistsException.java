package aj.FiTracker.FiTracker.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ZoneAlreadyExistsException extends HttpException{

    public ZoneAlreadyExistsException(Exception exception) {
        super(HttpStatus.CONFLICT, exception);
    }
}
