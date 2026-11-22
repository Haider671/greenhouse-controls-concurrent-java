/*
Haider Ali
 * Assignment 4
 *
 * PowerOn.java
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Repair class used to restore power after a greenhouse power outage.
 */
public class PowerOn implements Fixable {

    private final GreenhouseControls controller;

    /**
     * Creates a PowerOn repair object.
     *
     * @param controller greenhouse controller to repair
     */
    public PowerOn(GreenhouseControls controller) {
        this.controller = controller;
    }

    /**
     * Restores greenhouse power and clears the error code.
     */
    @Override
    public void fix() {
        controller.setVariable("poweron", true);
        controller.setVariable("errorcode", 0);
    }

    /**
     * Records the power repair in fix.log.
     */
    @Override
    public void log() {

        String message =
                "Time: " + Calendar.getInstance().getTime()
                        + "\nFix: Power restored";

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