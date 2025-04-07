package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Exceptions.ErrorResponseDTO;
import aj.FiTracker.FiTracker.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO errorResponse =new ErrorResponseDTO(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ZoneAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            ZoneAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO errorResponse =new ErrorResponseDTO(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ZoneDoesntExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            ZoneDoesntExistException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO errorResponse =new ErrorResponseDTO(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            UserUnauthorizedException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO errorResponse =new ErrorResponseDTO(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}