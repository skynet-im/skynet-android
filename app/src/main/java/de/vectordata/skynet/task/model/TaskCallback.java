package de.vectordata.skynet.task.model;


public interface TaskCallback<T> {

    void onTaskUpdate(T task);

}
