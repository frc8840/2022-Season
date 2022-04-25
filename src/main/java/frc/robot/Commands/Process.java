package frc.robot.Commands;

import java.util.HashMap;

/**
 * Process is a way to simplify the process of creating a command.
 * Might be useful for autonomous, or for creating a command from a button.
 *
 * @author JaidenAGrimminck
 * */
public class Process {
    private static final HashMap<String, Process> processes = new HashMap<>();

    /**
     * Gets a process by name
     * @param name The name of the process
     *             (case-sensitive)
     *             (if the process doesn't exist, returns null)
     * @return The process
     * */
    public static Process getProcess(String name) {
        return processes.get(name);
    }

    private final String name;

    /**
     * Creates an empty process.
     * @param name The name of the process
     * */
    public Process(String name) {
        this.name = name;

        addToList();
    }

    /**
     * Creates an empty process.
     * @param name The name of the process
     * @param autoRun Whether to run the process automatically
     * */
    public Process(String name, boolean autoRun) {
        this.name = name;
        if (autoRun) run();

        addToList();
    }

    /**
     * Add the process to the list of processes.
     * */
    private void addToList() {
        processes.put(name, this);
    }

    /**
     * Run the process. Usually a first frame, or an initial run.
     * */
    public void run() {}

    /**
     * Run the process every frame/update.
     * */
    public void runFrame() {}

    /**
     * Get the name of the process.
     * @return The name of the process.
     * */
    public String getName() { return name; }
}
