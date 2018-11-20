package de.vectordata.skynet.task.model;

public class TaskProgress {

    private int value;
    private boolean indeterminate;

    public TaskProgress() {
        this.value = -1;
        this.indeterminate = true;
    }

    public TaskProgress(int value) {
        this.value = value;
        this.indeterminate = false;
    }

    public int getValue() {
        return value;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }
}
