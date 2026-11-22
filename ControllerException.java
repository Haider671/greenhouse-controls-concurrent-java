/*
 * Haider Ali
 *
 * ControllerException.java
 */

/**
 * Exception thrown when a greenhouse event causes a failure.
 */
public class ControllerException extends Exception {

    /**
     * Creates a ControllerException with a message.
     *
     * @param message description of the greenhouse failure
     */
    public ControllerException(String message) {
        super(message);
    }
}