/*
 * Haider Ali
 *
 * LightOn.java
 */

/**
 * Event that turns the greenhouse light on.
 */

public class LightOn extends Event {

    /**
     * Creates a LightOn event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public LightOn(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse light on.
     */
    @Override
    public void action() {
        controller.setVariable("light", true);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Light is on";
    }
}