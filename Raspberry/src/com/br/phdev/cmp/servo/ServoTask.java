package com.br.phdev.cmp.servo;

import com.br.phdev.cmp.task.FlavorTaskGroup;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.cmp.Timer;
import com.br.phdev.cmp.task.TaskGroup;

public class ServoTask implements Task {

    private Servo servo;
    private double startPosition;
    private double currentPos;
    private int targetPos;
    private double step;
    private long delay;
    private long currentTime;
    private Timer timer;

    private boolean startTask;
    private boolean taskOver;

    private TaskListener taskListener[];

    private static long currentTaskId = 0;
    private final long taskId;

    private FlavorTaskGroup flavorTaskGroup;

    private boolean jusForDelay;

    private ServoTask(Servo servo, int targetPosDegrees, long delayInMilli, FlavorTaskGroup flavorTaskGroup) {
        this.servo = servo;
        this.targetPos = targetPosDegrees;
        this.delay = delayInMilli;
        this.currentTime = 0;
        this.timer = new Timer();
        this.startTask = false;
        this.taskOver = false;
        this.taskId = currentTaskId++;
        this.flavorTaskGroup = flavorTaskGroup;
        this.jusForDelay = false;
    }

    public ServoTask(Servo servo, int targetPosDegrees, long delayInMilli, TaskListener taskListener[], FlavorTaskGroup flavorTaskGroup) {
        this(servo, targetPosDegrees, delayInMilli, flavorTaskGroup);
        this.taskListener = taskListener;
    }

    public ServoTask(FlavorTaskGroup flavorTaskGroup, long delayInMilli) {
        this.taskId = currentTaskId++;
        this.delay = delayInMilli;
        this.flavorTaskGroup = flavorTaskGroup;
        this.jusForDelay = true;
    }

    @Override
    public long getTaskId() {
        return this.taskId;
    }

    @Override
    public void startTask() {
        if (!this.jusForDelay) {
            if (this.servo.getTaskSlave() == -1 && !taskOver) {
                if (this.flavorTaskGroup.isMyTurn()) {
                    this.startPosition = servo.getCurrentPositionDegrees();
                    this.currentPos = servo.getCurrentPositionDegrees();
                    if (this.delay > 0)
                        this.step = (targetPos - this.currentPos) / this.delay;
                    else
                        this.step = (targetPos - this.currentPos);
                    this.startTask = true;
                    this.timer.start();
                    this.servo.setTaskSlave(this.taskId);
                }
            }
        } else {
            waitFor(this.delay);
            this.taskOver = true;
        }
    }

    @Override
    public void doTask() {
        if (!this.taskOver && this.startTask) {
            //System.out.println("executando tarefa");
            if (this.timer.getTicksInMilliSeconds() >= this.currentTime) {
                if (this.delay > 0) {
                    this.currentPos = this.step * this.currentTime;
                    this.servo.move(this.startPosition + this.currentPos, false);
                } else {
                    this.servo.move(this.targetPos, false);
                    this.currentPos = this.targetPos;
                }
                this.currentTime += 100;
                if (this.currentTime > this.delay) {
                    if (this.targetPos != this.currentPos)
                        this.servo.move(this.targetPos, false);
                    this.taskOver = true;
                    this.startTask = false;
                    if (this.taskListener != null) {
                        for (int i=0; i<this.taskListener.length; i++) {
                            if (taskListener[i] != null)
                                this.taskListener[i].onServoTaskComplete(this.targetPos);
                        }
                    }
                    this.flavorTaskGroup.taskCompleted();
                }
            }
        }
    }

    @Override
    public void deleteTask() {
        if (!this.jusForDelay) {
            this.timer.stop();
            this.timer = null;
            this.servo.setTaskSlave(-1);
        }
        this.taskListener = null;
    }

    @Override
    public boolean isTaskOver() {
        return this.taskOver;
    }

    @Override
    public boolean isTaskStarted() {
        return this.startTask;
    }

    private static void waitFor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
