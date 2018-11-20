package de.vectordata.skynet.task.model;

public abstract class TaskWithResult<T> extends Task {

    private T result;

    public final T getResult() {
        return result;
    }

    public final void setResult(T result) {
        this.result = result;
    }

}
