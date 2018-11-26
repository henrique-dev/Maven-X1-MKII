package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.misc.Vector2D;

public class Member implements Motion {

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

    public void setFinalVector(Vector2D area) {
        this.finalVector = area;
    }

    @Override
    public void moveX(float x) {

    }

    @Override
    public void moveY(float y) {

    }

    @Override
    public void moveZ(float z) {
        this.servo.move(z);
    }

    @Override
    public void rotate(float degrees) {

    }
}
