package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.misc.Vector3D;

public class Member implements Motion {

    protected float length;

    protected Vector3D origin;
    protected Vector3D area;

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

    public Vector3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3D origin) {
        this.origin = origin;
    }

    public Vector3D get() {
        return area;
    }

    public void set(Vector3D area) {
        this.area = area;
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
