/*
 * Haider Ali

 *
 * WaterOn.java
 */

/**
 * Event that turns the greenhouse water on.
 */
public class WaterOn extends Event {

    /**
     * Creates a WaterOn event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public WaterOn(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse water on.
     */
    @Override
    public void action() {
        controller.setVariable("water", true);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Greenhouse water is on";
    }
}