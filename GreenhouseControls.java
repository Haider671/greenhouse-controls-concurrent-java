import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/*
 * Haider Ali
 * GreenhouseControls.java
 */

/**
 * Controls greenhouse state and concurrent event execution.
 * The class manages event threads, reflection-based event loading,
 * suspension and resumption, emergency shutdown, and saved state.
 */

public class GreenhouseControls implements Serializable {

    private final List<TwoTuple<String, Object>> variables =
            new ArrayList<TwoTuple<String, Object>>();


    public interface OutputListener {
        void append(String message);
    }

    private transient OutputListener outputListener;
    /**
     * Sets the listener that receives greenhouse output.
     *
     * @param listener listener used to display output
     */
    public void setOutputListener(OutputListener listener) {
        outputListener = listener;
    }

    private transient List<Thread> eventThreads =
            new ArrayList<Thread>();

    private transient Object pauseLock = new Object();

    private volatile boolean suspended = false;
    private volatile boolean running = false;

    private File eventsFile;
    /**
     * Creates a greenhouse controller with the default state values.
     */

    public GreenhouseControls() {

        variables.add(
                new TwoTuple<String, Object>("light", false));

        variables.add(
                new TwoTuple<String, Object>("water", false));

        variables.add(
                new TwoTuple<String, Object>(
                        "thermostat", "Day"));

        variables.add(
                new TwoTuple<String, Object>("fans", false));

        variables.add(
                new TwoTuple<String, Object>(
                        "windowok", true));

        variables.add(
                new TwoTuple<String, Object>(
                        "poweron", true));

        variables.add(
                new TwoTuple<String, Object>(
                        "errorcode", 0));
    }
    /**
     * Updates or adds a greenhouse state variable.
     *
     * @param name name of the variable
     * @param value new value of the variable
     */
    public synchronized void setVariable(
            String name, Object value) {

        for (int i = 0; i < variables.size(); i++) {

            TwoTuple<String, Object> variable =
                    variables.get(i);

            if (variable.first.equals(name)) {

                variables.set(
                        i,
                        new TwoTuple<String, Object>(
                                name, value));

                return;
            }
        }

        variables.add(
                new TwoTuple<String, Object>(
                        name, value));
    }
    /**
     * Returns the value of a greenhouse state variable.
     *
     * @param name name of the variable
     * @return current value, or null if the variable does not exist
     */
    public synchronized Object getVariable(
            String name) {

        for (TwoTuple<String, Object> variable
                : variables) {

            if (variable.first.equals(name)) {
                return variable.second;
            }
        }

        return null;
    }
    /**
     * Returns the current greenhouse error code.
     *
     * @return current error code
     */
    public int getError() {

        Object value = getVariable("errorcode");

        if (value instanceof Integer) {
            return (Integer) value;
        }

        return 0;
    }
    /**
     * Creates and starts a new thread for an event.
     *
     * @param event event to start
     */
    public synchronized void startEvent(Event event) {

        initializeTransientFields();

        Thread thread = new Thread(event);

        eventThreads.add(thread);

        running = true;

        thread.start();
    }
    /**
     * Waits for the specified event delay while supporting suspension.
     *
     * @param delayTime delay in milliseconds
     * @throws InterruptedException if the event thread is interrupted
     */
    public void waitForDelay(long delayTime)
            throws InterruptedException {

        initializeTransientFields();

        long remaining = delayTime;

        while (remaining > 0) {

            synchronized (pauseLock) {

                while (suspended) {
                    pauseLock.wait();
                }
            }

            long slice = Math.min(remaining, 100);

            long start =
                    System.currentTimeMillis();

            Thread.sleep(slice);

            long elapsed =
                    System.currentTimeMillis() - start;

            if (!suspended) {
                remaining -= elapsed;
            }
        }

        synchronized (pauseLock) {
            while (suspended) {
                pauseLock.wait();
            }
        }
    }
    /**
     * Suspends execution of greenhouse events.
     */
    public void suspendEvents() {
        suspended = true;
    }
    /**
     * Resumes suspended greenhouse events.
     */
    public void resumeEvents() {

        initializeTransientFields();

        synchronized (pauseLock) {

            suspended = false;

            pauseLock.notifyAll();
        }
    }
    /**
     * Stops all currently running event threads.
     */
    public synchronized void stopEvents() {

        initializeTransientFields();

        for (Thread thread : eventThreads) {
            thread.interrupt();
        }

        eventThreads.clear();

        running = false;

        synchronized (pauseLock) {

            suspended = false;

            pauseLock.notifyAll();
        }
    }
    /**
     * Requests termination of all currently running greenhouse events.
     */
    public synchronized void requestTermination() {

        initializeTransientFields();

        Thread current =
                Thread.currentThread();

        for (Thread thread
                : new ArrayList<Thread>(
                eventThreads)) {

            if (thread != current) {
                thread.interrupt();
            }
        }

        eventThreads.clear();

        running = false;

        synchronized (pauseLock) {

            suspended = false;

            pauseLock.notifyAll();
        }
    }
    /**
     * Removes a completed event thread from the active thread collection.
     *
     * @param thread event thread that has finished
     */
    public synchronized void eventFinished(
            Thread thread) {

        initializeTransientFields();

        eventThreads.remove(thread);

        if (eventThreads.isEmpty()) {
            running = false;
        }
    }
    /**
     * Returns whether greenhouse event execution is suspended.
     *
     * @return true if events are suspended
     */
    public boolean isRunning() {
        return running;
    }

    public boolean isSuspended() {
        return suspended;
    }
    /**
     * Sets the events file used by this greenhouse.
     *
     * @param file events file
     */
    public void setEventsFile(File file) {
        eventsFile = file;
    }
    /**
     * Returns the current events file.
     *
     * @return current events file
     */
    public File getEventsFile() {
        return eventsFile;
    }
    /**
     * Reads the selected events file, creates Event objects using
     * reflection, and starts each Event in its own thread.
     *
     * @throws Exception if the events file cannot be read or an event
     *                   class cannot be created
     */
    public void loadAndStartEvents()
            throws Exception {

        if (eventsFile == null) {

            throw new FileNotFoundException(
                    "No events file selected");
        }

        Scanner scanner =
                new Scanner(eventsFile);

        while (scanner.hasNextLine()) {

            String line =
                    scanner.nextLine().trim();

            if (line.length() == 0) {
                continue;
            }

            String[] parts =
                    line.split("[=,]");

            if (parts.length < 4
                    || !parts[0].trim()
                    .equals("Event")
                    || !parts[2].trim()
                    .equals("time")) {

                scanner.close();

                throw new IllegalArgumentException(
                        "Invalid event line: "
                                + line);
            }

            String eventName =
                    parts[1].trim();

            long delayTime =
                    Long.parseLong(
                            parts[3].trim());

            Class<?> eventClass =
                    Class.forName(eventName);

            if (!Event.class
                    .isAssignableFrom(
                            eventClass)) {

                scanner.close();

                throw new IllegalArgumentException(
                        eventName
                                + " is not an Event");
            }

            Constructor<?> constructor =
                    eventClass.getConstructor(
                            long.class,
                            GreenhouseControls.class);

            Event event =
                    (Event)
                            constructor.newInstance(
                                    delayTime,
                                    this);

            startEvent(event);
        }

        scanner.close();
    }
    /**
     * Sends a completed event description to the console and GUI.
     *
     * @param event event whose description is displayed
     */
    public void outputEvent(Event event) {

        String message =
                "[" + Thread.currentThread().getName() + "]"
                        + "[" + event.getDelayTime() + "] "
                        + event;

        if (outputListener != null) {
            outputListener.append(message + "\n");
        }

        System.out.println(message);
    }
    /**
     * Sends a general message to the console and GUI.
     *
     * @param message message to display
     */
    public void outputMessage(String message) {

        if (outputListener != null) {
            outputListener.append(message + "\n");
        }

        System.out.println(message);
    }
    /**
     * Handles an event failure by reporting it and starting
     * emergency shutdown.
     *
     * @param exception event failure
     */
    public void handleEventFailure(
            ControllerException exception) {

        outputMessage(
                "Emergency Shutdown! "
                        + exception.getMessage());

        emergencyShutdown(
                exception.getMessage());
    }

    private void initializeTransientFields() {

        if (eventThreads == null) {
            eventThreads =
                    new ArrayList<Thread>();
        }

        if (pauseLock == null) {
            pauseLock = new Object();
        }
    }
    /**
     * Restores a GreenhouseControls object from a serialized dump file.
     *
     * @param file dump file to read
     * @return restored greenhouse controller
     * @throws Exception if the saved object cannot be restored
     */
    public static GreenhouseControls readDump(File file)
            throws Exception {

        ObjectInputStream input =
                new ObjectInputStream(
                        new FileInputStream(file));

        GreenhouseControls restored =
                (GreenhouseControls) input.readObject();

        input.close();

        restored.initializeTransientFields();
        restored.running = false;
        restored.suspended = false;

        return restored;
    }
    /**
     * Saves the current greenhouse state to a serialized file.
     *
     * @param file destination dump file
     * @throws IOException if the file cannot be written
     */
    public void saveDump(File file)
            throws IOException {

        ObjectOutputStream output =
                new ObjectOutputStream(
                        new FileOutputStream(file));

        output.writeObject(this);
        output.close();
    }
    /**
     * Logs an emergency, saves the greenhouse state, and stops its events.
     *
     * @param reason reason for the emergency shutdown
     */
    public void emergencyShutdown(String reason) {

        try {
            java.io.PrintWriter output =
                    new java.io.PrintWriter(
                            new java.io.FileWriter(
                                    "error.log", true));

            output.println(
                    "Time: "
                            + java.util.Calendar
                            .getInstance()
                            .getTime());

            output.println(
                    "Reason: " + reason);
            output.println();
            output.close();

        } catch (java.io.IOException e) {

            outputMessage("Could not write error.log");
        }

        try {
            saveDump(
                    new File("dump.out"));
            outputMessage("Greenhouse state saved to dump.out");

        } catch (java.io.IOException e) {

            outputMessage("Could not save dump.out: " + e.getMessage());
        }
        stopEvents();
    }
    /**
     * Returns the repair object associated with an error code.
     *
     * @param errorCode greenhouse error code
     * @return appropriate Fixable object, or null if no repair is required
     */
    public Fixable getFixable(int errorCode) {

        if (errorCode == 1) {
            return new FixWindow(this);
        }

        if (errorCode == 2) {
            return new PowerOn(this);
        }

        return null;
    }
    /**
     * Returns the current greenhouse state.
     *
     * @return formatted greenhouse state
     */
    @Override
    public synchronized String toString() {

        return "Light: "
                + getVariable("light")
                + "\nWater: "
                + getVariable("water")
                + "\nThermostat: "
                + getVariable("thermostat")
                + "\nFans: "
                + getVariable("fans")
                + "\nWindow OK: "
                + getVariable("windowok")
                + "\nPower on: "
                + getVariable("poweron")
                + "\nError code: "
                + getVariable("errorcode");
    }
}