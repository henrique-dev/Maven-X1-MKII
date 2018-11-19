package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;
import com.br.phdev.misc.Log;

public class Servo {

    private final PCA9685 module;
    private final ServoData servoData;
    private int currentPositionDegrees;

    public Servo(PCA9685 module, ServoData servoData, int currentPositionDegrees) {
        this.module = module;
        this.servoData = servoData;
        this.currentPositionDegrees = currentPositionDegrees;
    }

    public ServoData getServoData() {
        return servoData;
    }

    public int getCurrentPositionDegrees() {
        return currentPositionDegrees;
    }

    @Deprecated
    public void setRawPosition(float position) {
        if (position >= 100 && position <= 650) {
            module.setPWM(this.servoData.getLocalChannel(), 0, (int)position);
        }
    }

    @Deprecated
    public void moveToRawDegrees(int degrees) {
        float newPos;
        if (this.servoData.isInverted()) {
            newPos = this.servoData.getMidPosition() - (this.servoData.getStep() * (float)degrees);
        } else {
            newPos = this.servoData.getMidPosition() + (this.servoData.getStep() * (float)degrees);
        }
        if (newPos >= this.servoData.getMinPosition() && newPos <= this.servoData.getMaxPosition())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)newPos);
        else
            Log.e("Nova posição fora da faixa: " + newPos);
    }

    public boolean move(int degrees) {
        return false;
    }

    public void moveToMin() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMinPosition());
    }

    public void moveToMid() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMidPosition());
    }

    public void moveToMax() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMaxPosition());
    }

    public void moveToLimitMin() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getStep() * (float)this.servoData.getLimitMin()));
    }

    public void moveToLimitMax() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getStep() * (float)this.servoData.getLimitMax()));
    }

    public void moveMaxUp() {
        if (this.servoData.isInverted())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * (float)this.servoData.getLimitMin())));
        else
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * (float)this.servoData.getLimitMax())));
    }

    public void moveMaxDown() {
        if (this.servoData.isInverted())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * (float)this.servoData.getLimitMax())));
        else
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * (float)this.servoData.getLimitMin())));
    }

    private static void waitFor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
