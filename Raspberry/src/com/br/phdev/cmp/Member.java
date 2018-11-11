package com.br.phdev.cmp;

public class Member {

    private Servo servo;

    public Member(Servo servo) {
        this.servo = servo;
    }

    public void setPosition(int position) {
        this.servo.setPosition(position);
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

}
