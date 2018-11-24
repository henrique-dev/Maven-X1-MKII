package com.br.phdev.cmp;

import com.br.phdev.data.LegData;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class Leg implements Motion {

    private boolean onGround;

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
        this.onGround = true;
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

        this.base.setLength(legData.getBaseLength());
        this.base.setOriginVector(this.originVector);
        this.base.setLengthVector(Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(this.originVector));

        this.femur.setLength(legData.getFemurLength());
        this.femur.setOriginVector(this.base.getLengthVector());
        this.femur.setLengthVector(Vector2D.createByMagAngle(
                Math.cos(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength(),
                legData.getLegMidDegrees()).addMe(this.base.getLengthVector()));

        this.tarsus.setLength(legData.getTarsusLength());
        this.tarsus.setOriginVector(this.femur.getLengthVector());
        this.tarsus.setLengthVector(Vector2D.createByMagAngle(
                Math.sin(Math.toRadians(tarsus.getServo().getCurrentPositionDegrees())) * tarsus.getLength(),
                legData.getLegMidDegrees()).addMe(this.femur.getLengthVector()));

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

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void moveX(float x) {

    }

    @Override
    public void moveY(float y) {

    }

    @Override
    public void moveZ(float z) {
        double currentHeight = this.tarsus.getLengthVector().subtract(base.getOriginVector()).getSize();
        Log.w("Altura atuak: " + currentHeight);
    }

    @Override
    public void rotate(float degrees) {

    }

}
