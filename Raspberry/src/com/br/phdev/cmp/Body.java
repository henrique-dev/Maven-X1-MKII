package com.br.phdev.cmp;

public class Body {

    private final int LEG_FRONT_LEFT = 0;
    private final int LEG_MID_RIGHT = 2;
    private final int LEG_BACK_LEFT = 4;

    private final int LEG_FRONT_RIGHT = 1;
    private final int LEG_MID_LEFT = 3;
    private final int LEG_BACK_RIGHT = 5;

    private Leg[] legs;

    public Body(Leg[] legs) {
        this.legs = legs;
    }


}
