/*
 * Haider Ali
 *
 * Terminate.java
 */

/**
 * Event that terminates the currently running greenhouse events.
 */
public class Terminate extends Event {

    /**
     * Creates a Terminate event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public Terminate(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Requests termination of the greenhouse event threads.
     */
    @Override
    public void action() {
        controller.requestTermination();
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Terminating";
    }
}