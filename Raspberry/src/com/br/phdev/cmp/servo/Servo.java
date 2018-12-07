package com.br.phdev.cmp.servo;

import com.br.phdev.data.ServoData;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.misc.Log;

public class Servo {

    private final PCA9685 module;
    private final ServoData servoData;
    private double currentPositionDegrees;

    private long taskSlave;

    public Servo(PCA9685 module, ServoData servoData, float currentPositionDegrees, boolean moveServoToMid) {
        this.module = module;
        this.servoData = servoData;
        this.currentPositionDegrees = currentPositionDegrees;
        this.taskSlave = -1;
        if (moveServoToMid)
            this.initServo();
    }

    private void initServo() {
        this.setRawPosition(0);
        waitFor(100);
        this.move(0, false);
        waitFor(300);
        this.setRawPosition(0);
        waitFor(100);
    }

    public long getTaskSlave() {
        return this.taskSlave;
    }

    public void setTaskSlave(long taskSlave) {
        this.taskSlave = taskSlave;
    }

    public ServoData getServoData() {
        return servoData;
    }

    public double getCurrentPositionDegrees() {
        return this.currentPositionDegrees;
    }

    public void setCurrentPositionDegrees(float currentPositionDegrees) {
        this.currentPositionDegrees = currentPositionDegrees;
    }

    @Deprecated
    public void setRawPosition(float position) {
        if (position >= 100 && position <= 650 || position == 0) {
            module.setPWM(this.servoData.getLocalChannel(), 0, (int)position);
        }
    }

    public void setRawPosition(int on, int off) {
        if (on >= 100 && on <= 650 || on == 0)
            if (off >= 100 && off <= 650 || off == 0)
                module.setPWM(this.servoData.getLocalChannel(), on, off);
    }

    public void move(double degrees, boolean justForCorrection) {
        if (degrees >= this.servoData.getLimitMin() && degrees <= this.servoData.getLimitMax()) {
            if (this.servoData.isInverted()) {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() - (this.servoData.getStep() * degrees)));
            } else {
                this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)(this.servoData.getMidPosition() + (this.servoData.getStep() * degrees)));
            }
            if (!justForCorrection)
                this.currentPositionDegrees = degrees;
        } else {
            Log.w("Posição ultrapassa os limites");
            if (degrees > this.servoData.getLimitMax()) {
                if (this.servoData.isInverted()) {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() - (this.servoData.getStep() * (float) this.servoData.getLimitMax())));
                } else {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() + (this.servoData.getStep() * (float) this.servoData.getLimitMax())));
                }
                if (!justForCorrection)
                    this.currentPositionDegrees = this.servoData.getLimitMax();
            } else if (degrees < this.servoData.getLimitMin()) {
                if (this.servoData.isInverted()) {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() - (this.servoData.getStep() * (float) this.servoData.getLimitMin())));
                } else {
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, (int) (this.servoData.getMidPosition() + (this.servoData.getStep() * (float) this.servoData.getLimitMin())));
                }
                if (!justForCorrection)
                    this.currentPositionDegrees = this.servoData.getLimitMin();
            }
        }
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
