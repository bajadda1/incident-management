package ma.bonmyd.backendincident.exceptions;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int statusCode;
    private String message;
    private String description;

    public ErrorResponse(int statusCode, String message, String description) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
    }
}
