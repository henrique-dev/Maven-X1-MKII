package com.br.phdev.members;

import com.br.phdev.data.BodyData;
import com.br.phdev.misc.Vector2D;

public class Body {

    public static final int LEG_FRONT_LEFT = 0;
    public static final int LEG_MID_RIGHT = 2;
    public static final int LEG_BACK_LEFT = 4;

    public static final int LEG_FRONT_RIGHT = 1;
    public static final int LEG_MID_LEFT = 3;
    public static final int LEG_BACK_RIGHT = 5;

    private double currentHeightToFloor;

    private Vector2D area;

    private BodyData bodyData;

    private Leg[] legs;

    public Body(Leg[] legs, BodyData bodyData) {
        this.legs = legs;
        this.bodyData = bodyData;
        this.currentHeightToFloor = 0;
    }

    public Vector2D getArea() {
        return area;
    }

    public void setArea(Vector2D area) {
        this.area = area;
    }

    public Leg getLeg(int legId) {
        return this.legs[legId];
    }

    public Leg[] getLegs() {
        return legs;
    }

    public void setLegs(Leg[] legs) {
        this.legs = legs;
    }

    public double getCurrentHeightToFloor() {
        return currentHeightToFloor;
    }

    public void setCurrentHeightToFloor(double currentHeightToFloor) {
        this.currentHeightToFloor = currentHeightToFloor;
    }

    public void stay() {
        for (Leg leg : legs)
            leg.stay();
    }
}
