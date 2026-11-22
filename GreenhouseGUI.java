import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

/*
 * Haider Ali *
 * GreenhouseGUI.java
 */

/**
 * Graphical user interface for controlling a GreenhouseControls object.
 * Each window manages its own independent greenhouse controller.
 */


public class GreenhouseGUI extends JFrame {

    private static final java.util.List<GreenhouseGUI> windows =
            new java.util.ArrayList<GreenhouseGUI>();

    private GreenhouseControls controller;

    private final JTextArea outputArea =
            new JTextArea(20, 60);

    private final JButton startButton =
            new JButton("Start");

    private final JButton restartButton =
            new JButton("Restart");

    private final JButton terminateButton =
            new JButton("Terminate");

    private final JButton suspendButton =
            new JButton("Suspend");

    private final JButton resumeButton =
            new JButton("Resume");

    private JMenuItem newWindowItem;
    private JMenuItem closeWindowItem;
    private JMenuItem openEventsItem;
    private JMenuItem restoreItem;
    private JMenuItem exitItem;

    private JMenuItem popupStartItem;
    private JMenuItem popupRestartItem;
    private JMenuItem popupTerminateItem;
    private JMenuItem popupSuspendItem;
    private JMenuItem popupResumeItem;
    /**
     * Creates and displays a new greenhouse control window.
     */
    public GreenhouseGUI() {

        super("Greenhouse Controls");

        controller = new GreenhouseControls();
        windows.add(this);

        outputArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(outputArea);

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(startButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(terminateButton);
        buttonPanel.add(suspendButton);
        buttonPanel.add(resumeButton);

        setLayout(new BorderLayout());

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        createMenuBar();

        newWindowItem.addActionListener(
                e -> new GreenhouseGUI());

        closeWindowItem.addActionListener(
                e -> closeWindow());

        createPopupMenu();

        // Start button and popup Start item use the same method.
        startButton.addActionListener(
                e -> startAction());

        popupStartItem.addActionListener(
                e -> startAction());

        suspendButton.addActionListener(e -> suspendAction());
        popupSuspendItem.addActionListener(e -> suspendAction());

        resumeButton.addActionListener(e -> resumeAction());
        popupResumeItem.addActionListener(e -> resumeAction());

        terminateButton.addActionListener(e -> terminateAction());
        popupTerminateItem.addActionListener(e -> terminateAction());

        restartButton.addActionListener(e -> restartAction());
        popupRestartItem.addActionListener(e -> restartAction());

        restoreItem.addActionListener(
                e -> restoreAction());

        Timer controlTimer =
                new Timer(200, e -> updateControls());

        controlTimer.start();

        updateControls();

        controller.setOutputListener(
                new GreenhouseControls.OutputListener() {

                    @Override
                    public void append(String message) {

                        SwingUtilities.invokeLater(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        outputArea.append(message);
                                    }
                                });
                    }
                });

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE);

        exitItem.addActionListener(
                e -> exitApplication());

        addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        closeWindow();
                    }
                });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * Restores a saved greenhouse from a dump file and repairs
     * the recorded malfunction when possible.
     */
    private void restoreAction() {

        if (controller.isRunning()) {
            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file =
                chooser.getSelectedFile();

        try {

            GreenhouseControls restored =
                    GreenhouseControls.readDump(file);

            controller = restored;

            controller.setOutputListener(
                    new GreenhouseControls.OutputListener() {

                        @Override
                        public void append(String message) {

                            SwingUtilities.invokeLater(
                                    () -> outputArea.append(message));
                        }
                    });

            Fixable fixer =
                    controller.getFixable(
                            controller.getError());

            if (fixer != null) {
                fixer.fix();
                fixer.log();
            }

            outputArea.append(
                    "Restored greenhouse from: "
                            + file.getName()
                            + "\n");

            outputArea.append(
                    controller.toString()
                            + "\n");

            updateControls();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not restore file: "
                            + e.getMessage(),
                    "Restore Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Exits the application, warning the user if any greenhouse
     * window is still running.
     */
    private void exitApplication() {

        boolean anyRunning = false;

        for (GreenhouseGUI window : windows) {

            if (window.controller.isRunning()) {
                anyRunning = true;
                break;
            }
        }

        if (anyRunning) {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "One or more greenhouses are running. Exit anyway?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        for (GreenhouseGUI window :
                new java.util.ArrayList<GreenhouseGUI>(windows)) {

            window.controller.stopEvents();
            window.dispose();
        }

        windows.clear();

        System.exit(0);
    }
    /**
     * Closes the current GUI window, warning the user if its
     * greenhouse is still running.
     */
    private void closeWindow() {

        if (controller.isRunning()) {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "The greenhouse is running. Close anyway?",
                            "Confirm Close",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        controller.stopEvents();

        windows.remove(this);

        dispose();

        if (windows.isEmpty()) {
            System.exit(0);
        }
    }
    /**
     * Updates buttons and menu items according to the current
     * greenhouse running and suspended state.
     */
    private void updateControls() {

        boolean running =
                controller.isRunning();

        boolean suspended =
                controller.isSuspended();

        boolean hasFile =
                controller.getEventsFile() != null;

        // Main buttons
        startButton.setEnabled(
                !running && hasFile);

        restartButton.setEnabled(
                !running && hasFile);

        terminateButton.setEnabled(
                running);

        suspendButton.setEnabled(
                running && !suspended);

        resumeButton.setEnabled(
                running && suspended);

        // Popup menu items
        popupStartItem.setEnabled(
                !running && hasFile);

        popupRestartItem.setEnabled(
                !running && hasFile);

        popupTerminateItem.setEnabled(
                running);

        popupSuspendItem.setEnabled(
                running && !suspended);

        popupResumeItem.setEnabled(
                running && suspended);

        // Restore cannot be used while running
        restoreItem.setEnabled(
                !running);
    }
    /**
     * Restarts the greenhouse using the current events file.
     */
    private void restartAction() {

        if (controller.isRunning()) {
            return;
        }

        if (controller.getEventsFile() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please open an events file first.",
                    "No Events File",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        controller.startEvent(
                new Restart(0, controller));

        outputArea.append("Restarting greenhouse\n");
    }
    /**
     * Prompts for a delay and schedules a Terminate event.
     */
    private void terminateAction() {

        if (!controller.isRunning()) {
            return;
        }

        String input =
                JOptionPane.showInputDialog(
                        this,
                        "Enter termination delay in milliseconds:");

        if (input == null) {
            return;
        }

        try {

            long delay =
                    Long.parseLong(input.trim());

            if (delay < 0) {
                throw new NumberFormatException();
            }

            controller.startEvent(
                    new Terminate(delay, controller));

            outputArea.append(
                    "Terminate scheduled in "
                            + delay
                            + " ms\n");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid non-negative number.",
                    "Invalid Delay",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Suspends all currently running greenhouse event threads.
     */
    private void suspendAction() {

        if (!controller.isRunning()) {
            return;
        }

        controller.suspendEvents();

        outputArea.append("Greenhouse suspended\n");
    }
    /**
     * Resumes all suspended greenhouse event threads.
     */
    private void resumeAction() {

        if (!controller.isRunning()) {
            return;
        }

        controller.resumeEvents();

        outputArea.append("Greenhouse resumed\n");
    }
    /**
     * Starts all events from the selected events file.
     */
    private void startAction() {

        if (controller.getEventsFile() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please open an events file first.",
                    "No Events File",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        try {

            controller.loadAndStartEvents();

            outputArea.append(
                    "Greenhouse started\n");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not start events: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Opens and validates an events file selected by the user.
     */
    private void openEvents() {

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            File file =
                    chooser.getSelectedFile();

            try {

                Scanner scanner =
                        new Scanner(file);

                while (scanner.hasNextLine()) {

                    String line =
                            scanner.nextLine().trim();

                    if (line.length() == 0) {
                        continue;
                    }

                    String[] parts =
                            line.split("[=,]");

                    if (parts.length < 4
                            || !parts[0].trim().equals("Event")
                            || !parts[2].trim().equals("time")) {

                        scanner.close();

                        throw new IllegalArgumentException(
                                "Invalid event file");
                    }

                    Long.parseLong(
                            parts[3].trim());
                }

                scanner.close();

                controller.setEventsFile(file);

                outputArea.append(
                        "Opened events file: "
                                + file.getName()
                                + "\n");
                updateControls();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid event file: "
                                + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * Creates the File menu and its keyboard shortcuts.
     */
    private void createMenuBar() {

        JMenuBar menuBar =
                new JMenuBar();

        JMenu fileMenu =
                new JMenu("File");

        newWindowItem =
                new JMenuItem("New Window");

        closeWindowItem =
                new JMenuItem("Close Window");

        openEventsItem =
                new JMenuItem("Open Events");

        restoreItem =
                new JMenuItem("Restore");

        exitItem =
                new JMenuItem("Exit");

        newWindowItem.setAccelerator(
                KeyStroke.getKeyStroke("control N"));

        closeWindowItem.setAccelerator(
                KeyStroke.getKeyStroke("control W"));

        openEventsItem.setAccelerator(
                KeyStroke.getKeyStroke("control O"));

        restoreItem.setAccelerator(
                KeyStroke.getKeyStroke("control R"));

        exitItem.setAccelerator(
                KeyStroke.getKeyStroke("control Q"));

        openEventsItem.addActionListener(
                e -> openEvents());

        fileMenu.add(newWindowItem);
        fileMenu.add(closeWindowItem);
        fileMenu.addSeparator();
        fileMenu.add(openEventsItem);
        fileMenu.add(restoreItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }
    /**
     * Creates the popup menu containing the five greenhouse controls.
     */
    private void createPopupMenu() {

        JPopupMenu popup =
                new JPopupMenu();

        popupStartItem =
                new JMenuItem("Start");

        popupRestartItem =
                new JMenuItem("Restart");

        popupTerminateItem =
                new JMenuItem("Terminate");

        popupSuspendItem =
                new JMenuItem("Suspend");

        popupResumeItem =
                new JMenuItem("Resume");

        popup.add(popupStartItem);
        popup.add(popupRestartItem);
        popup.add(popupTerminateItem);
        popup.addSeparator();
        popup.add(popupSuspendItem);
        popup.add(popupResumeItem);

        outputArea.setComponentPopupMenu(popup);
    }
    /**
     * Starts the greenhouse GUI application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        new GreenhouseGUI();
                    }
                });
    }
}