/*
Haider Ali
 *
 * Bell.java
 */

/**
 * Event that rings the greenhouse bell.
 */
public class Bell extends Event {

    /**
     * Creates a Bell event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public Bell(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Performs the bell event.
     */
    @Override
    public void action() {
        // Bell does not modify greenhouse state.
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Bing!";
    }
}