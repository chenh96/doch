package tech.chenh.doch.exception;

public class RegisterException extends Exception {
    
    public RegisterException(String message) {
        super(message);
    }
    
    public RegisterException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
