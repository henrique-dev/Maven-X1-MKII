package com.br.phdev.cmp;

import com.br.phdev.misc.Vector2D;

public class Body {

    public static final int LEG_FRONT_LEFT = 0;
    public static final int LEG_MID_RIGHT = 2;
    public static final int LEG_BACK_LEFT = 4;

    public static final int LEG_FRONT_RIGHT = 1;
    public static final int LEG_MID_LEFT = 3;
    public static final int LEG_BACK_RIGHT = 5;

    private BodyData bodyData;

    private Leg[] legs;

    private Vector2D width;
    private Vector2D length;
    private Vector2D height;

    public Body(Leg[] legs, BodyData bodyData) {
        this.legs = legs;
        this.bodyData = bodyData;
    }

    public Vector2D getWidth() {
        return width;
    }

    public void setWidth(Vector2D width) {
        this.width = width;
    }

    public Vector2D getLength() {
        return length;
    }

    public void setLength(Vector2D length) {
        this.length = length;
    }

    public Vector2D getHeight() {
        return height;
    }

    public void setHeight(Vector2D height) {
        this.height = height;
    }

    public double getLegHeigth(int legNumber) {
        return 0;
    }

    public double getLegLength(int legNumber) {
        return 0;
    }
}
