package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.data.LegData;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;
import com.br.phdev.misc.Vector3D;

public class Leg implements Motion {

    private boolean onGround;

    private final LegData legData;
    private final Base base;
    private final Femur femur;
    private final Tarsus tarsus;

    private Vector3D origin;

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

    public Vector3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3D origin) {
        this.origin = origin;

        base.setLength(legData.getBaseLength());
        base.setOrigin(origin);
        Vector2D baseXY = Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(origin.getVector2D());
        base.set(new Vector3D(baseXY.x, baseXY.y, origin.z));

        femur.setLength(legData.getFemurLength());
        femur.setOrigin(base.get());
        Vector2D femurXY = Vector2D.createByMagAngle(Math.cos(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength(),
                legData.getLegMidDegrees()).addMe(base.get().getVector2D());
        femur.set(new Vector3D(femurXY.x, femurXY.y, Math.sin(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength()));

        tarsus.setLength(legData.getTarsusLength());
        tarsus.setOrigin(femur.get());
        Vector2D tarsusXY = Vector2D.createByMagAngle(Math.sin(Math.toRadians(tarsus.getServo().getCurrentPositionDegrees())) * tarsus.getLength(),
                legData.getLegMidDegrees()).addMe(femur.get().getVector2D());
        tarsus.set(new Vector3D(tarsusXY.x, tarsusXY.y, tarsus.length));


        Log.w("Vetores da perna " + legData.getLegNumber() + " com inclinação de " + legData.getLegMidDegrees());
        Log.w("perna origin: " + origin.getVector2D());

        Log.w("Base origin xyz: " + this.base.getOrigin());
        Log.w("Base length xyz: " + this.base.get());

        Log.w("Femur origin: " + this.femur.getOrigin());
        Log.w("Femur length: " + this.femur.get());

        Log.w("Tarso origin: " + this.tarsus.getOrigin());
        Log.w("Tarso length: " + this.tarsus.get());

        Log.w("Comprimento total da perna: " + (this.tarsus.get().subtract(this.base.get())).getSize());

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
        Log.w("perna origin: " + this.originVector);

        Log.w("Base origin: " + this.base.getOriginVector());
        Log.w("Base length: " + this.base.getLengthVector());

        Log.w("Femur origin: " + this.femur.getOriginVector());
        Log.w("Femur length: " + this.femur.getLengthVector());

        Log.w("Tarso origin: " + this.tarsus.getOriginVector());
        Log.w("Tarso length: " + this.tarsus.getLengthVector());

        Log.w("Comprimento total da pena: " + (this.tarsus.getLengthVector().subtract(this.base.getOriginVector())).getSize());

    }*/

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

    }

    @Override
    public void rotate(float degrees) {

    }

}
