package aj.FiTracker.FiTracker.Exceptions;


import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ErrorResponseDTO {
    private final String message;
    private final String error;
    private final int statusCode;
    private final String timestamp;
    private final String path;


    public ErrorResponseDTO(HttpException exception, HttpServletRequest httpServletRequest) {
        this.message = exception.getMessage();
        this.statusCode = exception.getStatus().value();
        this.path = httpServletRequest.getRequestURI();
        this.error = exception.getStatus().getReasonPhrase();
        this.timestamp = getTime();
    }

    public ErrorResponseDTO(MethodArgumentNotValidException e, HttpServletRequest httpServletRequest) {
        this.message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        this.statusCode = HttpStatus.BAD_REQUEST.value();
        this.path = httpServletRequest.getRequestURI();
        this.error = HttpStatus.BAD_REQUEST.getReasonPhrase();
        this.timestamp = getTime();
    }

    public ErrorResponseDTO(HandlerMethodValidationException e, HttpServletRequest httpServletRequest) {
        this.message = e.getMessage();
        this.statusCode = HttpStatus.BAD_REQUEST.value();
        this.path = httpServletRequest.getRequestURI();
        this.error = HttpStatus.BAD_REQUEST.getReasonPhrase();
        this.timestamp = getTime();
    }

    public ErrorResponseDTO(Exception e, HttpServletRequest httpServletRequest) {
        this.message = e.getMessage();
        this.statusCode = 500;
        this.path = httpServletRequest.getRequestURI();
        this.error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        this.timestamp = getTime();
    }

    public ErrorResponseDTO(NoResourceFoundException e, HttpServletRequest httpServletRequest) {
        this.message = e.getMessage();
        this.statusCode = 404;
        this.path = httpServletRequest.getRequestURI();
        this.error = HttpStatus.NOT_FOUND.getReasonPhrase();
        this.timestamp = getTime();
    }


    private String getTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
