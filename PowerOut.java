/*
 * Haider Ali
 *
 * PowerOut.java
 */

/**
 * Event that simulates a greenhouse power outage.
 */
public class PowerOut extends Event {

    /**
     * Creates a PowerOut event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public PowerOut(
            long delayTime,
            GreenhouseControls controller) {

        super(delayTime, controller);
    }

    /**
     * Records the power outage and throws an exception
     * so the greenhouse begins emergency shutdown.
     *
     * @throws ControllerException whenever the power outage occurs
     */
    @Override
    public void action() throws ControllerException {

        controller.setVariable("errorcode", 2);
        controller.setVariable("poweron", false);

        throw new ControllerException(
                "Power outage!");
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Power out!";
    }
}