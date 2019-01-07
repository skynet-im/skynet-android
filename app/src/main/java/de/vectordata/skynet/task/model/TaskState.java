package de.vectordata.skynet.task.model;

public enum TaskState {
    SCHEDULED,
    RUNNING,
    SLEEPING,
    FAILED,
    SUCCESS;

    public boolean isFinished() {
        return this == FAILED || this == SUCCESS;
    }
}
