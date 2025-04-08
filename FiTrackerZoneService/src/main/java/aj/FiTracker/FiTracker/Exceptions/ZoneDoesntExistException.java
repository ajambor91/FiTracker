package aj.FiTracker.FiTracker.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ZoneDoesntExistException extends HttpException {

    public ZoneDoesntExistException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
