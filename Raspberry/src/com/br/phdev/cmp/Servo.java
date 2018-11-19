package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;
import com.br.phdev.misc.Log;

public class Servo {

    private final PCA9685 module;
    private final ServoData servoData;
    private float currentPositionDegrees;

    public Servo(PCA9685 module, ServoData servoData, float currentPositionDegrees) {
        this.module = module;
        this.servoData = servoData;
        this.currentPositionDegrees = currentPositionDegrees;
    }

    public ServoData getServoData() {
        return servoData;
    }

    public float getCurrentPositionDegrees() {
        return currentPositionDegrees;
    }

    public void setCurrentPositionDegrees(float currentPositionDegrees) {
        this.currentPositionDegrees = currentPositionDegrees;
    }

    @Deprecated
    public void setRawPosition(float position) {
        if (position >= 100 && position <= 650) {
            module.setPWM(this.servoData.getLocalChannel(), 0, (int)position);
        }
    }

    public boolean move(float degrees) {
        if (degrees >= this.servoData.getLimitMin() && degrees <= this.servoData.getLimitMax()) {
            if (this.servoData.isInverted()) {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() - (this.servoData.getStep() * degrees)));
            } else {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * degrees)));
            }
            this.currentPositionDegrees = degrees;
            return true;
        } else {
            Log.w("Posição ultrapassa os limites");
            if (degrees > this.servoData.getLimitMax()) {
                if (this.servoData.isInverted()) {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() - (this.servoData.getStep() * (float) this.servoData.getLimitMax())));
                } else {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() + (this.servoData.getStep() * (float) this.servoData.getLimitMax())));
                }
                this.currentPositionDegrees = this.servoData.getLimitMax();
            } else if (degrees < this.servoData.getLimitMin()) {
                if (this.servoData.isInverted()) {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() - (this.servoData.getStep() * (float) this.servoData.getLimitMin())));
                } else {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() + (this.servoData.getStep() * (float) this.servoData.getLimitMin())));
                }
                this.currentPositionDegrees = this.servoData.getLimitMin();
            }
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

    public void moveMaxUp() {
        if (this.servoData.isInverted())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() - (this.servoData.getStep() * (float)this.servoData.getLimitMax())));
        else
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * (float)this.servoData.getLimitMax())));

    }

    public void moveMaxDown() {
        if (this.servoData.isInverted())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() - (this.servoData.getStep() * (float)this.servoData.getLimitMin())));
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
