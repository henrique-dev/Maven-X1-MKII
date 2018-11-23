package com.br.phdev.cmp;

import com.br.phdev.misc.Vector2D;

public class Leg {

    private final LegData legData;
    private final Base base;
    private final Femur femur;
    private final Tarsus tarsus;

    private Vector2D originVector;

    public Leg(LegData legData, Base base, Femur femur, Tarsus tarsus) {
        this.legData = legData;
        this.base = base;
        this.femur = femur;
        this.tarsus = tarsus;
    }

    public LegData getLegData() {
        return legData;
    }

    public Base getBase() {
        return base;
    }

    public Femur getFemur() {
        return femur;
    }

    public Tarsus getTarsus() {
        return tarsus;
    }

    public Vector2D getOriginVector() {
        return originVector;
    }

    public void setOriginVector(Vector2D originVector) {
        this.originVector = originVector;
        this.base.setOriginVector(this.originVector);
        this.base.setLengthVector(Vector2D.createByMagAngle(15, 45));
        System.out.println(this.base.getLengthVector());
    }

}
