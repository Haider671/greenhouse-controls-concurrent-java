/*
 * Haider Ali
 * Restart.java
 */

import java.io.File;

/**
 * Event that restarts the greenhouse using the current events file.
 */
public class Restart extends Event {

    private final File file;

    /**
     * Creates a Restart event using the controller's current events file.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     */
    public Restart(long delayTime, GreenhouseControls controller) {
        this(delayTime, controller, controller.getEventsFile());
    }

    /**
     * Creates a Restart event using a specified events file.
     *
     * @param delayTime delay before execution in milliseconds
     * @param controller greenhouse controller
     * @param file events file to reload
     */
    public Restart(
            long delayTime,
            GreenhouseControls controller,
            File file) {

        super(delayTime, controller);
        this.file = file;
    }

    /**
     * Reloads and starts all events from the selected file.
     *
     * @throws ControllerException if the events file cannot be loaded
     */
    @Override
    public void action() throws ControllerException {

        if (file == null) {
            throw new ControllerException(
                    "No events file selected");
        }

        try {
            controller.setEventsFile(file);
            controller.loadAndStartEvents();

        } catch (Exception e) {
            throw new ControllerException(
                    "Could not restart events: "
                            + e.getMessage());
        }
    }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    @Override
    public String toString() {
        return "Restarting System";
    }
}