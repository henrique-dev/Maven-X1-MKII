package com.br.phdev.members;

import com.br.phdev.cmp.Motion;
import com.br.phdev.data.BodyData;
import com.br.phdev.misc.Vector3D;

public class Body implements Motion {

    public static final int LEG_FRONT_LEFT = 0;
    public static final int LEG_MID_RIGHT = 2;
    public static final int LEG_BACK_LEFT = 4;

    public static final int LEG_FRONT_RIGHT = 1;
    public static final int LEG_MID_LEFT = 3;
    public static final int LEG_BACK_RIGHT = 5;

    private Vector3D area;

    private BodyData bodyData;

    private Leg[] legs;

    public Body(Leg[] legs, BodyData bodyData) {
        this.legs = legs;
        this.bodyData = bodyData;
    }

    public Vector3D getArea() {
        return area;
    }

    public void setArea(Vector3D area) {
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
        for (Leg leg : legs) {
            if (leg.isOnGround())
                leg.moveZ(z);
        }
    }

    @Override
    public void rotate(float degrees) {

    }
}
