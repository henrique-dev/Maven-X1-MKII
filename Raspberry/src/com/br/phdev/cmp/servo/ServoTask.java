package com.br.phdev.cmp.servo;

import com.br.phdev.cmp.Task;
import com.br.phdev.cmp.Timer;
import com.br.phdev.misc.Log;

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
        Log.w("nova tardefa");
        Log.w("Posição atual: " + currentPos + " -> posição alvo: " + targetPos + " -> step: " + step);
    }

    public ServoTask start() {
        this.startTask = true;
        this.timer.start();
        return this;
    }

    @Override
    public void doTask() {
        if (!this.startTask) {
            this.startTask = true;
            this.timer.start();
            Log.w("a tarefa sera iniciada");
        }
        if (!isTaskOver() && this.startTask) {
            if (this.timer.getTicksInMilliSeconds() >= this.currentTime) {
                this.currentPos = this.step * this.currentTime;
                this.servo.move(this.currentPos);
                this.currentTime += 100;
                System.out.println("executando tarefa " + this.currentTime + " - " + this.currentPos);
            }
        }
    }

    @Override
    public void deleteTask() {
        this.timer.stop();
        this.timer = null;
    }

    @Override
    public boolean isTaskOver() {
        if (this.currentTime >= this.timer.getTicksInMilliSeconds()) {
            if (this.targetPos != this.currentPos)
                this.servo.move(this.targetPos);
            return true;
        }
        return false;
    }
}
