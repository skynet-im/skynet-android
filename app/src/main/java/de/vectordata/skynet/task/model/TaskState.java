package de.vectordata.skynet.task.model;

public enum TaskState {
    SCHEDULED,
    RUNNING,
    SLEEPING,
    FAILED,
    SUCCESS;

    public boolean isFinished() {
        return ordinal() == FAILED.ordinal() || ordinal() == SUCCESS.ordinal();
    }
}
