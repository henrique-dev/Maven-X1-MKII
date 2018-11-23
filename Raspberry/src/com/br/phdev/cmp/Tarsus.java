package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.misc.Vector2D;

public class Tarsus extends Member {

    private Vector2D originVector;
    private Vector2D lengthVector;

    public Tarsus(Servo servo) {
        super(servo);
    }

    public Vector2D getOriginVector() {
        return originVector;
    }

    public void setOriginVector(Vector2D originVector) {
        this.originVector = originVector;
    }

    public Vector2D getLengthVector() {
        return lengthVector;
    }

    public void setLengthVector(Vector2D lengthVector) {
        this.lengthVector = lengthVector;
    }

}
