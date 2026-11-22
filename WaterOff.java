/*
 * Haider Ali
 * Assignment 4

 * WaterOff.java
 */

/**
 * Event that turns the greenhouse water off.
 */
public class WaterOff extends Event {

    /**
     * Creates a WaterOff event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public WaterOff(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse water off.
     */
    @Override
    public void action() {
        controller.setVariable("water", false);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Greenhouse water is off";
    }
}