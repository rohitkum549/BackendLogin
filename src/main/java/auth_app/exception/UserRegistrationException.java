package auth_app.exception;

public class UserRegistrationException extends RuntimeException {
    private final String errorCode;

    public UserRegistrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
