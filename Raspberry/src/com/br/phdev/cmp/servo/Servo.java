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
        moveToMid();
        waitFor(300);
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

    public void move(double degrees, boolean justForCorrection, boolean ignoreDegreesLimits) {
        int servoSignal;
        if (degrees >= this.servoData.getLimitMin() && degrees <= this.servoData.getLimitMax()) {
            if (this.servoData.isInverted()) {
                servoSignal =  (int)(this.servoData.getMidPosition() - getSignalFromDegrees(degrees) - getSignalFromDegrees(servoData.getMidCorrection()));
                this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
            } else {
                servoSignal = (int)(this.servoData.getMidPosition() + getSignalFromDegrees(degrees) + getSignalFromDegrees(servoData.getMidCorrection()));
                this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
            }
            if (!justForCorrection)
                this.currentPositionDegrees = degrees;
        } else {
            Log.w("Posição ultrapassa os limites");
            if (degrees > this.servoData.getLimitMax()) {
                if (this.servoData.isInverted()) {
                    if (ignoreDegreesLimits) {
                        servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(degrees) - getSignalFromDegrees(servoData.getMidCorrection()));
                        if (servoSignal < 150 || servoSignal > 600) {
                            servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(this.servoData.getLimitMax()) - getSignalFromDegrees(servoData.getMidCorrection()));
                            degrees = this.servoData.getLimitMax();
                        }
                    } else {
                        servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(this.servoData.getLimitMax()) - getSignalFromDegrees(servoData.getMidCorrection()));
                        degrees = this.servoData.getLimitMax();
                    }
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
                } else {
                    if (ignoreDegreesLimits) {
                        servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(degrees) + getSignalFromDegrees(servoData.getMidCorrection()));
                        if (servoSignal < 150 || servoSignal > 600) {
                            servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(this.servoData.getLimitMax()) + getSignalFromDegrees(servoData.getMidCorrection()));
                            degrees = this.servoData.getLimitMax();
                        }
                    } else {
                        servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(this.servoData.getLimitMax()) + getSignalFromDegrees(servoData.getMidCorrection()));
                        degrees = this.servoData.getLimitMax();
                    }
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
                }
            } else if (degrees < this.servoData.getLimitMin()) {
                if (this.servoData.isInverted()) {
                    if (ignoreDegreesLimits) {
                        servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(degrees) - getSignalFromDegrees(servoData.getMidCorrection()));
                        if (servoSignal < 150 || servoSignal > 600) {
                            servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(this.servoData.getLimitMin()) - getSignalFromDegrees(servoData.getMidCorrection()));
                            degrees = this.servoData.getLimitMin();
                        }
                    } else {
                        servoSignal = (int) (this.servoData.getMidPosition() - getSignalFromDegrees(this.servoData.getLimitMin()) - getSignalFromDegrees(servoData.getMidCorrection()));
                        degrees = this.servoData.getLimitMin();
                    }
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
                } else {
                    if (ignoreDegreesLimits) {
                        servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(degrees) + getSignalFromDegrees(servoData.getMidCorrection()));
                        if (servoSignal < 150 || servoSignal > 600) {
                            servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(this.servoData.getLimitMin()) + getSignalFromDegrees(servoData.getMidCorrection()));
                            degrees = this.servoData.getLimitMin();
                        }
                    } else {
                        servoSignal = (int) (this.servoData.getMidPosition() + getSignalFromDegrees(this.servoData.getLimitMin()) + getSignalFromDegrees(servoData.getMidCorrection()));
                        degrees = this.servoData.getLimitMin();
                    }
                    this.module.setPWM(this.servoData.getLocalChannel(), 0, servoSignal);
                }
            }
            if (!justForCorrection)
                this.currentPositionDegrees = degrees;
        }
    }

    private double getSignalFromDegrees(double degrees) {
        return (double)this.servoData.getStep() * degrees;
    }

    public void moveToMin() {
        this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMinPosition());
    }

    public void moveToMid() {
        if (this.servoData.isInverted())
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMidPosition() - (int)getSignalFromDegrees(servoData.getMidCorrection()));
        else
            this.module.setPWM(this.servoData.getLocalChannel(), 0, (int)this.servoData.getMidPosition() + (int)getSignalFromDegrees(servoData.getMidCorrection()));
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
