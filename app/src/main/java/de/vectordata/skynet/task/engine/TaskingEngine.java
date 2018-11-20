package de.vectordata.skynet.task.engine;

import android.util.Log;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import de.vectordata.skynet.task.model.Task;
import de.vectordata.skynet.task.model.TaskCallback;
import de.vectordata.skynet.task.model.TaskState;


public class TaskingEngine {

    private static final String TAG = "TaskingEngine";

    private Queue<Task> pendingTasks = new ConcurrentLinkedQueue<>();
    private List<Task> runningTasks = new CopyOnWriteArrayList<>();

    private List<TaskCallback> callbacks = new CopyOnWriteArrayList<>();

    private String name;

    public TaskingEngine(String name) {
        this.name = "[" + name + "] ";
    }

    public long scheduleTask(Task task) {
        Log.d(TAG, name + "Task scheduled: " + task.getClass().getSimpleName());
        pendingTasks.offer(task);
        update();
        return task.getId();
    }

    public void addCallback(TaskCallback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(TaskCallback callback) {
        callbacks.remove(callback);
    }

    public void onTaskUpdated(Task task) {
        Log.d(TAG, name + "Task " + task.getClass().getSimpleName() + " updated: {STATE=" + task.getState().name() + ", PROGRESS=" + task.getProgress().getValue() + "}");
        if (task.getState().isFinished()) {
            runningTasks.remove(task);
            Log.d(TAG, name + "Removed " + task.getClass().getSimpleName() + " from running tasks");
            update();
        }
        for (TaskCallback callback : callbacks) callback.onTaskUpdate(task);
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
