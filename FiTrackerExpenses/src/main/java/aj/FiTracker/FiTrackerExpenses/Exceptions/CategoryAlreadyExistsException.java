package aj.FiTracker.FiTrackerExpenses.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends HttpException {

    public CategoryAlreadyExistsException(Exception exception) {
        super(HttpStatus.CONFLICT, exception);
    }
}
