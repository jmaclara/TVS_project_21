package core.src.main.java.ap.exceptions;

/**
 * Thrown when a Client method would put the client in an invalid state.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String msg) { super(msg); }
}
