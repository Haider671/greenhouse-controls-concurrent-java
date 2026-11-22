/*
 * Haider Ali
 * Assignment 4
 *
 * ThermostatNight.java
 */

/**
 * Event that changes the greenhouse thermostat to the night setting.
 */
public class ThermostatNight extends Event {

    /**
     * Creates a ThermostatNight event.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public ThermostatNight(long delayTime, GreenhouseControls controller) {
        super(delayTime, controller);
    }

    /**
     * Changes the thermostat to the night setting.
     */
    @Override
    public void action() {
        controller.setVariable("thermostat", "Night");
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Thermostat on night setting";
    }
}