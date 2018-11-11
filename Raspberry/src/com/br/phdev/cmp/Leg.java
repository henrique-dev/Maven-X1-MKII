package com.br.phdev.cmp;

public class Leg {

    private LegData legData;
    private Base base;
    private Femur femur;
    private Tarsus tarsus;

    public Leg(LegData legData, Base base, Femur femur, Tarsus tarsus) {
        this.legData = legData;
        this.base = base;
        this.femur = femur;
        this.tarsus = tarsus;
    }
}
