package com.br.phdev.cmp;

public class ServoTask implements Task {

    private Servo servo;
    private float currentPos;
    private int targetPos;
    private float step;
    private long delay;
    private long currentTime;
    private Timer timer;

    private boolean startTask;

    public ServoTask(Servo servo, int targetPosDegrees, long delayInMilli) {
        this.servo = servo;
        this.targetPos = targetPosDegrees;
        this.currentPos = servo.getCurrentPositionDegrees();
        this.delay = delayInMilli;
        this.currentTime = 100;
        this.timer = new Timer();
        this.step = (targetPos - this.currentPos) / delayInMilli;
        this.startTask = false;
    }

    public ServoTask start() {
        this.startTask = true;
        this.timer.start();
        return this;
    }

    @Override
    public void doTask() {
        if (!this.startTask)
            this.startTask = true;
        if (!isTaskOver() && this.startTask) {
            if (this.timer.getTicksInMilliSeconds() >= this.currentTime) {
                System.out.println("executando tarefa");
                this.currentPos += this.step;
                this.servo.move(this.currentPos);
                this.currentTime += 100;
            }
        }
    }

    @Override
    public boolean isTaskOver() {
        if (step >= 0)
            return this.currentPos >= this.targetPos;
        else
            return this.currentPos <= this.targetPos;
    }
}
