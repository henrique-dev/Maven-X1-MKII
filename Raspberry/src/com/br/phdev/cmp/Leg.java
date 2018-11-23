package com.br.phdev.cmp;

import com.br.phdev.misc.Log;
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

        this.base.setLengthVector(Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(this.originVector));

        this.femur.setOriginVector(this.base.getLengthVector());
        this.femur.setLengthVector(Vector2D.createByMagAngle(legData.getFemurLength(), legData.getLegMidDegrees()).addMe(this.base.getLengthVector()));

        this.tarsus.setOriginVector(this.femur.getLengthVector());
        this.tarsus.setLengthVector(Vector2D.createByMagAngle(legData.getTarsusLength(), legData.getLegMidDegrees()).addMe(this.femur.getLengthVector()));

        Log.w("\n");

        Log.w("Vetores da perna " + legData.getLegNumber() + " com inclinação de " + legData.getLegMidDegrees());
        Log.w("perna origin: " + this.originVector);

        Log.w("Base origin: " + this.base.getOriginVector());
        Log.w("Base length: " + this.base.getLengthVector());

        Log.w("Femur origin: " + this.femur.getOriginVector());
        Log.w("Femur length: " + this.femur.getLengthVector());

        Log.w("Tarso origin: " + this.tarsus.getOriginVector());
        Log.w("Tarso length: " + this.tarsus.getLengthVector());

        Log.w("Comprimento total da pena: " + (this.tarsus.getLengthVector().subtract(this.base.getOriginVector())).getSize());
    }

}
