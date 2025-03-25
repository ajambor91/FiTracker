package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Exceptions.ErrorResponseDTO;
import aj.FiTracker.FiTracker.Exceptions.UserAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.UserDoesntExistException;
import aj.FiTracker.FiTracker.Exceptions.UserUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

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

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO errorResponse =new ErrorResponseDTO(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserDoesntExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            UserDoesntExistException ex,
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