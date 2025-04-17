package aj.FiTracker.FiTrackerExpenses.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CannotFindCategoriesException extends HttpException {

    public CannotFindCategoriesException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
