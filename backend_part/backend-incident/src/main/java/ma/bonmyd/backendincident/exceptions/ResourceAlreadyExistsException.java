package ma.bonmyd.backendincident.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Annotate with @ResponseStatus to set default HTTP status
//@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
