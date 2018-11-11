package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;

public class Servo {

    private final PCA9685 module;
    private ServoData servoData;
    private int currentPosition;

    public Servo(PCA9685 module, ServoData servoData, int currentPosition) {
        this.module = module;
        this.servoData = servoData;
        this.currentPosition = currentPosition;
    }

    @Deprecated
    public void setRawPosition(int position) {
        System.out.println(servoData.toString());
        if (position > 150 && position < 600) {
            module.setPWM(this.servoData.getLocalChannel(), 0, position);
        }
    }

    public void setPosition(int position) {
        if (this.servoData.getMaxPosition() > this.servoData.getMinPosition()) {
            if (position >= this.servoData.getMinPosition() && position <= this.servoData.getMaxPosition() || position == 0) {
                module.setPWM(this.servoData.getLocalChannel(), 0, position);
            }
        } else {
            if (position >= this.servoData.getMaxPosition() && position <= this.servoData.getMinPosition() || position == 0) {
                module.setPWM(this.servoData.getLocalChannel(), 0, position);
            }
        }
    }

    public boolean move(int offset) {
        int newPos = this.currentPosition + offset;
        if (this.servoData.getMaxPosition() > this.servoData.getMinPosition()) {
            if (newPos >= this.servoData.getMinPosition() && newPos <= this.servoData.getMaxPosition()) {
                module.setPWM(this.servoData.getLocalChannel(), 0, newPos);
                return true;
            }
        } else {
            if (newPos >= this.servoData.getMaxPosition() && newPos <= this.servoData.getMinPosition()) {
                module.setPWM(this.servoData.getLocalChannel(), 0, newPos);
                return true;
            }
        }
        return false;
    }

    public void moveToMin() {

    }

    public void moveToMid() {
        int gch = this.servoData.getGlobalChannel();
        if (gch == 12 || gch == 14 || gch == 8 || gch == 7 || gch == 6) {
            System.out.println("Movendo o servo " + this.servoData.getGlobalChannel() + " para a posição " + this.servoData.getMidPosition());
            this.module.setPWM(this.servoData.getLocalChannel(), 0, this.servoData.getMidPosition());
            waitFor(1000);
        }
    }

    public void moveToMax() {

    }

    private static void waitFor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
