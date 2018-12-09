package com.br.phdev.cmp.task;

public interface Task {

    long getTaskId();
    void executeNow();
    void startTask();
    void doTask();
    void deleteTask();
    boolean isTaskOver();
    boolean isTaskStarted();

}
