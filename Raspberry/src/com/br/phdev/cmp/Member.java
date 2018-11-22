package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.Servo;

public class Member {

    protected float length;
    protected Servo servo;

    Member(Servo servo) {
        this.servo = servo;
    }

    public boolean move(float degrees) {
        return this.servo.move(degrees);
    }

    public void moveToMin() {
        this.servo.moveToMin();
    }

    public void moveToMid() {
        this.servo.moveToMid();
    }

    public void moveToMax() {
        this.servo.moveToMax();
    }

    public Servo getServo() {
        return servo;
    }
}
