package com.br.phdev.cmp;

import com.br.phdev.misc.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LegsControl {

    private Leg[] legs;
    private Servo[] servos;

    private List<Task> taskList;
    private LinkedList<Task> taskListBuffer;

    private MainThread mainThread;

    public LegsControl(Leg[] legs, Servo[] servos) {
        this.legs = legs;
        this.servos = servos;
        this.taskList = new ArrayList<>();
        this.taskListBuffer = new LinkedList<>();

    }

    public void start() {
        if (this.mainThread == null) {
            Log.w("Iniciando sistema das pernas");
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
            if (this.taskListBuffer != null)
                this.taskListBuffer.clear();
            this.taskListBuffer = null;
            if (this.taskList != null)
                this.taskList.clear();
            this.taskList = null;
            Log.w("Encerrando sistema das pernas");
        }
    }

    private synchronized LinkedList<Task> getBufferTaskList() {
        return this.taskListBuffer;
    }

    public void addTask(Task task) {
        this.getBufferTaskList().add(task);
    }

    public void addTasks(List<Task> taskList) {
        this.getBufferTaskList().addAll(taskList);
        Log.w("tarefas para servo adicionada. tamanho da lista: " + this.getBufferTaskList().size());
    }

    private class MainThread extends Thread {

        private boolean runningMainLoop;

        @Override
        public void run() {
            this.runningMainLoop = true;
            Log.w("iniciando thread\n");
            while (this.runningMainLoop) {
                for (Task task : LegsControl.this.taskList) {
                    task.doTask();
                }
                for (int i=0; i<LegsControl.this.taskList.size(); i++) {
                    Task task = LegsControl.this.taskList.get(i);
                    if (task.isTaskOver()) {
                        LegsControl.this.taskList.remove(i);
                        i -= 1;
                    }
                }
                LegsControl.this.taskList.addAll(getBufferTaskList());
                getBufferTaskList().clear();
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
