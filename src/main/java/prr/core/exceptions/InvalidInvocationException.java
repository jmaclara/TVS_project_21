package prr.core.exceptions;

/**
 * Thrown when a Terminal method is invoked in an invalid state.
 */
public class InvalidInvocationException extends RuntimeException {
    public InvalidInvocationException(String msg) { super(msg); }
}