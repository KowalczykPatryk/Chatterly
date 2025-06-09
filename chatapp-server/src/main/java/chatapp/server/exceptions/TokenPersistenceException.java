package chatapp.server.exceptions;

public class TokenPersistenceException extends Exception {
    private final String code;
    public TokenPersistenceException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    public String getCode() { return code; }
}