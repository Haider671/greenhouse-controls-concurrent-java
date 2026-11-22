/*
 * Haider Ali
 *
 * FansOff.java
 */

/**
 * Event that turns the greenhouse fans off.
 */
public class FansOff extends Event {

    /**
     * Creates a FansOff event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public FansOff(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse fans off.
     */
    @Override
    public void action() {
        controller.setVariable("fans", false);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "FansOff";
    }
}