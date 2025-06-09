package chatapp.server.exceptions;


public class TokenValidationException extends Exception {
    private final String code;
    public TokenValidationException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() { return code; }
}