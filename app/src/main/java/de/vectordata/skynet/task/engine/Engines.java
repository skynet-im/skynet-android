package de.vectordata.skynet.task.engine;


public class Engines {

    private static Engines instance = new Engines();

    private TaskingEngine regularQueue;
    private TaskingEngine fileQueue;

    public static Engines getInstance() {
        return instance;
    }

    public TaskingEngine getRegularQueue() {
        return regularQueue;
    }

    public TaskingEngine getFileQueue() {
        return fileQueue;
    }

    public void initialize() {
        regularQueue = new TaskingEngine("Regular");
        fileQueue = new TaskingEngine("VSLFile");
    }
}
