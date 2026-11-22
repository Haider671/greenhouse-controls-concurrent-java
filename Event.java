import java.io.Serializable;

/**
 * Base class for all greenhouse events.
 * Each event runs in its own thread and controls its own delay time.
 */

public abstract class Event implements Runnable, Serializable {

    protected final long delayTime;
    protected final GreenhouseControls controller;

    /**
     * Creates an event with a specified delay and greenhouse controller.
     *
     * @param delayTime delay before the event executes, in milliseconds
     * @param controller greenhouse controller associated with the event
     */
    public Event(long delayTime, GreenhouseControls controller) {
        this.delayTime = delayTime;
        this.controller = controller;
    }

    /**
     * Returns the delay time for this event.
     *
     * @return event delay in milliseconds
     */
    protected long getDelayTime() {
        return delayTime;
    }

    /**
     * Waits for the event delay, performs the event action,
     * and sends the event description to the controller.
     */
    @Override
    public final void run() {
        try {
            controller.waitForDelay(delayTime);

            if (!Thread.currentThread().isInterrupted()) {
                action();
                controller.outputEvent(this);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } catch (ControllerException e) {
            controller.handleEventFailure(e);

        } finally {
            controller.eventFinished(Thread.currentThread());
        }
    }

    /**
     * Performs the specific action associated with this event.
     *
     * @throws ControllerException if the event causes a greenhouse failure
     */
    public abstract void action() throws ControllerException;
}