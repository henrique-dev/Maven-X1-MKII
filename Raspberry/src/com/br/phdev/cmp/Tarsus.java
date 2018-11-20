package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.Servo;

public class Tarsus extends Member {

    private float groundPositionDegrees;

    public Tarsus(Servo servo) {
        super(servo);
    }

    public float getGroundPositionDegrees() {
        return groundPositionDegrees;
    }

    public void setGroundPositionDegrees(float groundPositionDegrees) {
        if (super.getServo().move(groundPositionDegrees))
            this.groundPositionDegrees = groundPositionDegrees;
        else
            this.groundPositionDegrees = super.getServo().getCurrentPositionDegrees();
    }
}
