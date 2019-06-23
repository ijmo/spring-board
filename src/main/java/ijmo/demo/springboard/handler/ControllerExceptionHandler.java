package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice(annotations = Controller.class)
public class ControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String notFound() {
        log.debug("Not found!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String unauthenticated() {
        log.debug("Unauthenticated!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String unauthorized() {
        log.debug("Unauthorized!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(CannotCreateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String cannotCreate() {
        log.debug("Cannot create!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(CannotUpdateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String cannotUpdate() {
        log.debug("Cannot update!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String cannotDelete() {
        log.debug("Cannot delete!");
        return DEFAULT_ERROR_VIEW;
    }
}
