package com.br.phdev.cmp.servo;

import com.br.phdev.cmp.task.Task;

import java.util.ArrayList;
import java.util.List;

public class ServoTaskController {

    private final List<Task> taskList;

    private MainThread mainThread;

    public ServoTaskController() {
        this.taskList = new ArrayList<>();
    }

    public void start() {
        if (this.mainThread == null) {
            this.mainThread = new MainThread();
            this.mainThread.start();
        }
    }

    public void stop() {
        try {
            if (this.mainThread != null) {
                this.mainThread.setRunningMainLoop(false);
                this.mainThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.mainThread = null;
            this.taskList.clear();

        }
    }

    public void addTask(Task task) {
        synchronized (this.taskList) {
            this.taskList.add(task);
        }
    }

    public void addTasks(List<Task> taskList) {
        synchronized (this.taskList) {
            this.taskList.addAll(taskList);
        }
    }

    private class MainThread extends Thread {

        private boolean runningMainLoop;

        @Override
        public void run() {
            this.runningMainLoop = true;
            while (this.runningMainLoop) {
                synchronized (ServoTaskController.this.taskList) {
                    for (int i=0; i<ServoTaskController.this.taskList.size(); i++) {
                        Task task = ServoTaskController.this.taskList.get(i);
                        if (!task.isTaskStarted() && !task.isTaskOver()) {
                            task.startTask();
                        }
                        task.doTask();
                        if (task.isTaskOver()) {
                            task.deleteTask();
                            ServoTaskController.this.taskList.remove(i);
                            i--;
                            if (ServoTaskController.this.taskList.isEmpty())
                                break;
                        }
                    }
                }
            }
        }

        public boolean isRunningMainLoop() {
            return runningMainLoop;
        }

        public void setRunningMainLoop(boolean runningMainLoop) {
            this.runningMainLoop = runningMainLoop;
        }
    }

}
