package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;

public class Servo {

    private final PCA9685 module;
    private final int globalChannel;
    private final int localChannel;
    private final int minPosition;
    private final int midPosition;
    private final int maxPosition;
    private int currentPosition;

    public Servo(PCA9685 module, int globalChannel, int localChannel, int minPosition, int midPosition, int maxPosition, int currentPosition) {
        this.module = module;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
        this.currentPosition = currentPosition;
    }

    public void setPosition(int position) {
        if (maxPosition > minPosition) {
            if (position >= minPosition && position <= maxPosition) {
                module.setPWM(this.localChannel, 0, position);
            }
        } else {
            if (position >= maxPosition && position <= minPosition) {
                module.setPWM(this.localChannel, 0, position);
            }
        }
    }

    public boolean move(int offset) {
        int newPos = this.currentPosition + offset;
        if (maxPosition > minPosition) {
            if (newPos >= minPosition && newPos <= maxPosition) {
                module.setPWM(this.localChannel, 0, newPos);
                return true;
            }
        } else {
            if (newPos >= maxPosition && newPos <= minPosition) {
                module.setPWM(this.localChannel, 0, newPos);
                return true;
            }
        }
        return false;
    }

    public void moveToMin() {

    }

    public void moveToMid() {

    }

    public void moveToMax() {

    }

}
