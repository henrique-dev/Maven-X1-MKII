package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.data.LegData;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class Leg {

    private boolean onGround;

    private final LegData legData;
    private final Base base;
    private final Femur femur;
    private final Tarsus tarsus;

    private Vector2D originVector;
    private Vector2D lengthVector;

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

    public Vector2D getLengthVector() {
        return lengthVector;
    }

    public void setOriginVector(Vector2D originVector) {
        this.originVector = originVector;
        base.setLength(legData.getBaseLength());
        base.setOriginVector(originVector);
        Vector2D baseXY = Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(originVector);
        base.setFinalVector(new Vector2D(baseXY.x, baseXY.y));

        femur.setLength(legData.getFemurLength());
        femur.setOriginVector(base.getFinalVector());
        Vector2D femurXY = Vector2D.createByMagAngle(Math.cos(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength(),
                legData.getLegMidDegrees()).addMe(base.getFinalVector());
        femur.setFinalVector(new Vector2D(femurXY.x, femurXY.y));

        tarsus.setLength(legData.getTarsusLength());
        tarsus.setOriginVector(femur.getFinalVector());
        Vector2D tarsusXY = Vector2D.createByMagAngle(Math.sin(Math.toRadians(tarsus.getServo().getCurrentPositionDegrees())) * tarsus.getLength(),
                legData.getLegMidDegrees()).addMe(femur.getFinalVector());
        tarsus.setFinalVector(new Vector2D(tarsusXY.x, tarsusXY.y));

        lengthVector = this.tarsus.getFinalVector().subtract(this.base.getOriginVector());

        Log.w("");
        Log.w("Vetores da perna " + legData.getLegNumber() + " com inclinação de " + legData.getLegMidDegrees());
        Log.w("Perna originVector: " + originVector);

        Log.w("Base originVector : " + this.base.getOriginVector());
        Log.w("Base lengthVector : " + this.base.getFinalVector());

        Log.w("Femur originVector: " + this.femur.getOriginVector());
        Log.w("Femur lengthVector: " + this.femur.getFinalVector());

        Log.w("Tarso originVector: " + this.tarsus.getOriginVector());
        Log.w("Tarso lengthVector: " + this.tarsus.getFinalVector());

        Log.w("Perna lengthVector: " + lengthVector);

        Log.w("Comprimento da perna em relação a origem da base: " + this.lengthVector.getSize());

    }

    /*
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
        Log.w("perna originVector: " + this.originVector);

        Log.w("Base originVector: " + this.base.getOriginVector());
        Log.w("Base length: " + this.base.getLengthVector());

        Log.w("Femur originVector: " + this.femur.getOriginVector());
        Log.w("Femur length: " + this.femur.getLengthVector());

        Log.w("Tarso originVector: " + this.tarsus.getOriginVector());
        Log.w("Tarso length: " + this.tarsus.getLengthVector());

        Log.w("Comprimento total da pena: " + (this.tarsus.getLengthVector().subtract(this.base.getOriginVector())).getSize());

    }*/

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void move(double angle, double finalLength, double precision) {
        this.base.move(angle);
        double xft = finalLength;
        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double precision
    }



}
