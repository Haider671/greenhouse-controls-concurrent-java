/*
 * Haider Ali
 *
 * ThermostatDay.java
 */

/**
 * Event that changes the greenhouse thermostat to the day setting.
 */
public class ThermostatDay extends Event {

    /**
     * Creates a ThermostatDay event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public ThermostatDay(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Changes the thermostat to the day setting.
     */
    @Override
    public void action() {
        controller.setVariable("thermostat", "Day");
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Thermostat on day setting";
    }
}