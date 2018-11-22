package com.br.phdev.cmp.task;

import com.br.phdev.misc.Log;

public class TaskGroup {

    private int taskGroups[];
    private int currentTaskGroupAmount;
    private int currentTaskGroup;

    public TaskGroup(int[] taskGroups) {
        this.taskGroups = taskGroups;
        this.currentTaskGroup = 0;
        if (taskGroups.length > 1)
            this.currentTaskGroupAmount = taskGroups[0];
        Log.w("Total de grupos: " + taskGroups.length);
    }

    public void taskCompleted() {
        Log.w("Executando o grupo " + this.currentTaskGroup);
        this.currentTaskGroupAmount--;
        if (this.currentTaskGroupAmount == 0) {
            this.currentTaskGroup++;
            if (this.currentTaskGroup < taskGroups.length) {
                this.currentTaskGroupAmount = taskGroups[this.currentTaskGroup];
            }
        }
    }

    public void finalizeTaskGroup() {
        this.taskGroups = null;
    }

    public int getCurrentTaskGroup() {
        return currentTaskGroup;
    }
}
