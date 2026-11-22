/*
 * Haider Ali
 *
 * FixWindow.java
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Repair class used to fix a greenhouse window malfunction.
 */
public class FixWindow implements Fixable {

    private final GreenhouseControls controller;

    /**
     * Creates a FixWindow repair object.
     *
     * @param controller greenhouse controller to repair
     */
    public FixWindow(GreenhouseControls controller) {
        this.controller = controller;
    }

    /**
     * Repairs the greenhouse window and clears the error code.
     */
    @Override
    public void fix() {
        controller.setVariable("windowok", true);
        controller.setVariable("errorcode", 0);
    }

    /**
     * Records the window repair in fix.log.
     */
    @Override
    public void log() {

        String message =
                "Time: " + Calendar.getInstance().getTime()
                        + "\nFix: Window restored";

        System.out.println(message);

        try {
            PrintWriter output =
                    new PrintWriter(
                            new FileWriter("fix.log", true));

            output.println(message);
            output.println();
            output.close();

        } catch (IOException e) {
            System.out.println("Could not write to fix.log");
        }
    }
}