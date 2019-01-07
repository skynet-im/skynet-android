package de.vectordata.skynet.task.model;

public class TaskHandle<T> {

    private long taskId;

    private Class<T> taskClass;

    private TaskCallback<T> callback;

    private boolean hasCondition;

    private TaskState condition;

    public TaskHandle(long taskId, Class<T> taskClass) {
        this.taskId = taskId;
        this.taskClass = taskClass;
    }

    public long getTaskId() {
        return taskId;
    }

    public Class<T> getTaskClass() {
        return taskClass;
    }

    public TaskCallback<T> getCallback() {
        return callback;
    }

    public boolean hasCondition() {
        return hasCondition;
    }

    public TaskState getCondition() {
        return condition;
    }

    public void awaitUpdate(TaskCallback<T> callback) {
        this.callback = callback;
    }

    public void awaitState(TaskState state, TaskCallback<T> callback) {
        this.hasCondition = true;
        this.condition = state;
        this.callback = callback;
    }
}
