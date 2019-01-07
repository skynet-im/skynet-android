package de.vectordata.skynet.task.engine;

import android.util.Log;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import de.vectordata.skynet.task.model.Task;
import de.vectordata.skynet.task.model.TaskHandle;
import de.vectordata.skynet.task.model.TaskState;


public class TaskingEngine {

    private static final String TAG = "TaskingEngine";

    private Queue<Task> pendingTasks = new ConcurrentLinkedQueue<>();
    private List<Task> runningTasks = new CopyOnWriteArrayList<>();

    private List<TaskHandle<? extends Task>> handles = new CopyOnWriteArrayList<>();

    private String name;

    TaskingEngine(String name) {
        this.name = "[" + name + "] ";
    }

    public <T extends Task> TaskHandle<T> scheduleTask(T task) {
        Log.d(TAG, name + "Task scheduled: " + task.getClass().getSimpleName());
        pendingTasks.offer(task);
        update();
        TaskHandle<T> handle = new TaskHandle<>(task.getId(), (Class<T>) task.getClass());
        handles.add(handle);
        return handle;
    }

    public void onTaskUpdated(Task task) {
        Log.d(TAG, name + "Task " + task.getClass().getSimpleName() + " updated: {STATE=" + task.getState().name() + ", PROGRESS=" + task.getProgress().getValue() + "}");
        if (task.getState().isFinished()) {
            runningTasks.remove(task);
            Log.d(TAG, name + "Removed " + task.getClass().getSimpleName() + " from running tasks");
            update();
        }
        for (TaskHandle handle : handles)
            if (handle.getTaskId() == task.getId() && handle.getTaskClass() == task.getClass() && (!handle.hasCondition() || handle.getCondition() == task.getState())) {
                if (handle.getCallback() != null) handle.getCallback().onTaskUpdate(task);
                if (task.getState().isFinished()) handles.remove(handle);
            }
    }

    private void update() {
        if (mayExecuteNewTask()) {
            Task nextTask = pendingTasks.poll();
            if (nextTask != null)
                executeTask(nextTask);
        }
    }

    private boolean mayExecuteNewTask() {
        for (Task task : runningTasks)
            if (task.getState() != TaskState.SLEEPING) return false;
        return true;
    }

    private void executeTask(Task task) {
        Log.d(TAG, name + "Executing task: " + task.getClass().getSimpleName());
        task.init(this);
        runningTasks.add(task);
        task.onExecute();
    }

}
