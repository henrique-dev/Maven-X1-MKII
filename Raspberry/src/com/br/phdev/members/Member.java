package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.misc.Vector3D;

public class Member implements Motion {

    protected float length;

    protected Vector3D originVector;
    protected Vector3D finalVector;

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

    public Vector3D getOriginVector() {
        return originVector;
    }

    public void setOriginVector(Vector3D originVector) {
        this.originVector = originVector;
    }

    public Vector3D getFinalVector() {
        return finalVector;
    }

    public void setFinalVector(Vector3D area) {
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
