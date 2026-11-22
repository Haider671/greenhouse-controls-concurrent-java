/*
 * Haider Ali
 *
 * LightOff.java
 */

/**
 * Event that turns the greenhouse light off.
 */
public class LightOff extends Event {

    /**
     * Creates a LightOff event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public LightOff(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse light off.
     */
    @Override
    public void action() {
        controller.setVariable("light", false);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Light is off";
    }
}