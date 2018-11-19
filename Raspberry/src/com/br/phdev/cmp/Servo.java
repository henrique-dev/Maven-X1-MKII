package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;

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

    public boolean move(int degrees) {
        float newPos = degrees * this.servoData.getStep();
        if (degrees > 0) {
            if ((int)newPos < this.servoData.getLimitMax()) {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)newPos);
                this.currentPositionDegrees = degrees;
            } else {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, this.servoData.getLimitMax());
                this.currentPositionDegrees = 90;
                return true;
            }
        } else if (degrees < 0){
            if ((int)newPos > this.servoData.getLimitMin()) {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)newPos);
                this.currentPositionDegrees = degrees;
            } else {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, this.getServoData().getLimitMin());
                this.currentPositionDegrees = -90;
                return true;
            }
        } else {
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.getServoData().getMidPosition());
            this.currentPositionDegrees = 0;
            return true;
        }
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

    private static void waitFor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
