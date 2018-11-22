package com.br.phdev.cmp.task;

public class FlavorTaskGroup {

    private TaskGroup taskGroup;
    private int myTaskGroup;

    public FlavorTaskGroup(int myTaskGroup, TaskGroup taskGroup) {
        this.myTaskGroup = myTaskGroup;
        this.taskGroup = taskGroup;
    }

    public void taskCompleted() {
        this.taskGroup.taskCompleted();
    }

    public void finalizeTaskGroup() {
        this.taskGroup.finalizeTaskGroup();
    }

    public boolean isMyTurn() {
        return this.myTaskGroup == this.taskGroup.getCurrentTaskGroup();
    }

}
