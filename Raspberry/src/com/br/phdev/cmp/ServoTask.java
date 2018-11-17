package com.br.phdev.cmp;

public class ServoTask implements Task {

    private Servo servo;
    private float currentPos;
    private int targetPos;
    private float step;
    private long delay;
    private long currentTime;

    public ServoTask(Servo servo, int targetPos, long delayInMilli) {
        this.servo = servo;
        this.targetPos = targetPos;
        this.currentPos = servo.getCurrentPositionDegrees();
        this.delay = delayInMilli;
        this.currentTime = 0;
        this.step = (targetPos - this.currentPos) / delayInMilli;
    }

    @Override
    public void doTask() {

    }

    @Override
    public boolean isTaskOver() {
        return this.currentPos >= this.targetPos;
    }
}
