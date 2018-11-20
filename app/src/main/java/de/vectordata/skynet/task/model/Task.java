package de.vectordata.skynet.task.model;
import java.util.Random;

import de.vectordata.skynet.task.engine.TaskingEngine;

public abstract class Task {

    private long id;
    private TaskState state;
    private TaskProgress progress;

    private TaskingEngine engine;

    public Task() {
        id = (new Random()).nextLong();
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
        this.progress = new TaskProgress();
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

}
