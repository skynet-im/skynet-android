package de.vectordata.skynet.task.model;

import java.util.Random;

import de.vectordata.skynet.task.engine.Engines;
import de.vectordata.skynet.task.engine.TaskingEngine;

public abstract class Task {

    private static Random idRandom = new Random();

    private long id;
    private TaskState state;
    private TaskProgress progress;

    private TaskingEngine engine;

    Task() {
        id = idRandom.nextLong();
    }

    public long getId() {
        return id;
    }

    public TaskState getState() {
        return state;
    }

    public TaskProgress getProgress() {
        return progress;
    }

    public abstract void onExecute();

    public final void init(TaskingEngine engine) {
        this.engine = engine;
        this.progress = TaskProgress.INDETERMINATE;
        setState(TaskState.RUNNING);
    }

    protected final void setState(TaskState state) {
        if (state != this.state) {
            this.state = state;
            engine.onTaskUpdated(this);
        }
    }

    protected final void reportProgress(TaskProgress taskProgress) {
        if (taskProgress.getValue() != this.getProgress().getValue()) {
            this.progress = taskProgress;
            engine.onTaskUpdated(this);
        }
    }

    protected void fail() {
        setState(TaskState.FAILED);
    }

    protected void success() {
        setState(TaskState.SUCCESS);
    }

    protected void sleep() {
        setState(TaskState.SLEEPING);
    }

    protected void wakeUp() {
        setState(TaskState.RUNNING);
    }

    protected Engines getEngines() {
        return Engines.getInstance();
    }

}
