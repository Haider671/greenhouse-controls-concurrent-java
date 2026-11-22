import java.io.*;

public class Assignment4Test {

    public static void main(String[] args) throws Exception {

        GreenhouseControls original =
                new GreenhouseControls();

        original.setVariable("light", true);
        original.setVariable("thermostat", "Night");

        File file = new File("testdump.out");

        ObjectOutputStream output =
                new ObjectOutputStream(
                        new FileOutputStream(file));

        output.writeObject(original);
        output.close();

        GreenhouseControls restored =
                GreenhouseControls.readDump(file);

        if (!restored.getVariable("light").equals(true)) {
            throw new AssertionError("Light was not restored");
        }

        if (!restored.getVariable("thermostat").equals("Night")) {
            throw new AssertionError("Thermostat was not restored");
        }

        System.out.println("Restore test passed");

        file.delete();
    }
}