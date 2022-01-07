package recipes.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RecipeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class, EmptyResultDataAccessException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        String responseBody = "Empty or missing mandatory fields for the object passed as argument";

        if (ex.getClass() == ConstraintViolationException.class || ex.getClass() == IllegalArgumentException.class) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (ex.getClass() == EmptyResultDataAccessException.class) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }


}
