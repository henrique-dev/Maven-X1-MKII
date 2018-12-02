package com.br.phdev.members;

import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.misc.Vector2D;

public class Member {

    protected float length;

    protected Vector2D originVector;
    protected Vector2D finalVector;

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

    public Vector2D getOriginVector() {
        return originVector;
    }

    public void setOriginVector(Vector2D originVector) {
        this.originVector = originVector;
    }

    public Vector2D getFinalVector() {
        return finalVector;
    }

    public double getCurrentAngle() {
        return this.servo.getCurrentPositionDegrees();
    }

    public void setFinalVector(Vector2D area) {
        this.finalVector = area;
    }

    public double getLimitMax() {
        return this.servo.getServoData().getLimitMax();
    }

    public double getLimitMin() {
        return this.servo.getServoData().getLimitMin();
    }

    public void move(double angle) {
        this.servo.move(angle);
    }

}
