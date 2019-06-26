package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void notFound() {
        log.debug("Not found!");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public void unauthenticated() {
        log.debug("Unauthenticated!");
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void unauthorized() {
        log.debug("Unauthorized!");
    }

    @ExceptionHandler(CannotCreateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void cannotCreate() {
        log.debug("Cannot create!");
    }

    @ExceptionHandler(CannotUpdateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void cannotUpdate() {
        log.debug("Cannot update!");
    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void cannotDelete() {
        log.debug("Cannot delete!");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, Object> bindFailed(BindException ex) {
        log.debug("Failed to bind attributes!");
        Map<String, Object> map = new HashMap<>();
        String message = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put("message", message);
        return map;
    }
}