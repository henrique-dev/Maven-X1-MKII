package com.br.phdev.cmp;

public class ServoTask implements Task {

    private Servo servo;
    private float currentPos;
    private int targetPos;
    private float step;
    private long delay;
    private long currentTime;
    private Timer timer;

    public ServoTask(Servo servo, int targetPosDegrees, long delayInMilli) {
        this.servo = servo;
        this.targetPos = targetPosDegrees;
        this.currentPos = servo.getCurrentPositionDegrees();
        this.delay = delayInMilli;
        this.currentTime = 0;
        this.timer = new Timer();
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
