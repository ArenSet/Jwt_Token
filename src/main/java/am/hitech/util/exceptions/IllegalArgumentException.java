package am.hitech.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalArgumentException extends Exception {

    public IllegalArgumentException() {
    }

    public IllegalArgumentException(String message) {
        super(message);
    }
}