package com.br.phdev.cmp.servo;

import com.br.phdev.cmp.task.FlavorTaskGroup;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.cmp.Timer;
import com.br.phdev.cmp.task.TaskGroup;

public class ServoTask implements Task {

    private Servo servo;
    private float startPosition;
    private float currentPos;
    private int targetPos;
    private float step;
    private long delay;
    private long currentTime;
    private Timer timer;

    private boolean startTask;
    private boolean taskOver;

    private TaskListener taskListener;

    private static long currentTaskId = 0;
    private final long taskId;

    private FlavorTaskGroup flavorTaskGroup;

    public ServoTask(Servo servo, int targetPosDegrees, long delayInMilli, TaskListener taskListener, FlavorTaskGroup flavorTaskGroup) {
        this.servo = servo;
        this.targetPos = targetPosDegrees;
        this.delay = delayInMilli;
        this.currentTime = 0;
        this.timer = new Timer();
        this.startTask = false;
        this.taskOver = false;
        this.taskListener = taskListener;
        this.taskId = currentTaskId++;
        this.flavorTaskGroup = flavorTaskGroup;
    }

    @Override
    public long getTaskId() {
        return this.taskId;
    }

    @Override
    public void startTask() {
        if (this.servo.getTaskSlave() == -1 && !taskOver) {
            if (this.flavorTaskGroup.isMyTurn()) {
                this.startPosition = servo.getCurrentPositionDegrees();
                this.currentPos = servo.getCurrentPositionDegrees();
                this.step = (targetPos - this.currentPos) / this.delay;
                this.startTask = true;
                this.timer.start();
                this.servo.setTaskSlave(this.taskId);
            }
        }
    }

    @Override
    public void doTask() {
        if (!this.taskOver && this.startTask) {
            if (this.timer.getTicksInMilliSeconds() >= this.currentTime) {
                this.currentPos = this.step * this.currentTime;
                this.servo.move(this.startPosition + this.currentPos);
                this.currentTime += 100;
                if (this.currentTime > this.delay) {
                    if (this.targetPos != this.currentPos)
                        this.servo.move(this.targetPos);
                    this.taskOver = true;
                    this.startTask = false;
                    if (this.taskListener != null)
                        this.taskListener.onServoTaskComplete(this.targetPos);
                    this.flavorTaskGroup.taskCompleted();
                }
            }
        }
    }

    @Override
    public void deleteTask() {
        this.timer.stop();
        this.timer = null;
        this.taskListener = null;
        this.servo.setTaskSlave(-1);
    }

    @Override
    public boolean isTaskOver() {
        return this.taskOver;
    }

    @Override
    public boolean isTaskStarted() {
        return this.startTask;
    }
}
