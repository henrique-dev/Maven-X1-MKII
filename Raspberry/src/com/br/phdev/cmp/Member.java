package com.br.phdev.cmp;

public class Member {

    private Servo servo;

    public Member(Servo servo) {
        this.servo = servo;
    }

    public boolean move(int offset) {
        return this.servo.move(offset);
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
