package de.vectordata.skynet.task.engine;


public class EngineManager {

    private static EngineManager instance = new EngineManager();

    private TaskingEngine regularQueue;
    private TaskingEngine fileQueue;

    public static EngineManager getInstance() {
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
