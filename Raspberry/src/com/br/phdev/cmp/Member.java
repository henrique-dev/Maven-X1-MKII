package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.Servo;

public class Member implements Motion {

    protected float length;
    protected Servo servo;

    Member(Servo servo) {
        this.servo = servo;
    }

    public Servo getServo() {
        return servo;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    @Override
    public void moveX(float x) {

    }

    @Override
    public void moveY(float y) {

    }

    @Override
    public void moveZ(float z) {

    }

    @Override
    public void rotate(float degrees) {

    }
}
