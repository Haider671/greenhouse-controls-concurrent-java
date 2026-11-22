/*
 * Haider Ali

 *
 * FansOn.java
 */

/**
 * Event that turns the greenhouse fans on.
 */
public class FansOn extends Event {

    /**
     * Creates a FansOn event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public FansOn(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Turns the greenhouse fans on.
     */
    @Override
    public void action() {
        controller.setVariable("fans", true);
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "FansOn";
    }
}