/*
 * Haider Ali
 *
 * WindowMalfunction.java
 */

/**
 * Event that simulates a greenhouse window malfunction.
 */
public class WindowMalfunction extends Event {

    /**
     * Creates a WindowMalfunction event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public WindowMalfunction(
            long delayTime,
            GreenhouseControls controller) {

        super(delayTime, controller);
    }

    /**
     * Records the window malfunction and throws an exception
     * so the greenhouse begins emergency shutdown.
     *
     * @throws ControllerException whenever the malfunction occurs
     */
    @Override
    public void action() throws ControllerException {

        controller.setVariable("errorcode", 1);
        controller.setVariable("windowok", false);

        throw new ControllerException(
                "Window malfunction!");
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Window malfunction";
    }
}