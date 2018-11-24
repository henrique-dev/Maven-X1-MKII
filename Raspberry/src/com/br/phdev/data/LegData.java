package com.br.phdev.data;

public class LegData {

    private final int legNumber;

    private final int baseServo;
    private final float baseLength;

    private final int femurServo;
    private final float femurLength;

    private final int tarsusServo;
    private final float tarsusLength;

    private final float legMidDegrees;

    public LegData(int legNumber, int baseServo, float baseLength, int femurServo, float femurLength, int tarsusServo, float tarsusLength, float legMidDegrees) {
        this.legNumber = legNumber;
        this.baseServo = baseServo;
        this.baseLength = baseLength;
        this.femurServo = femurServo;
        this.femurLength = femurLength;
        this.tarsusServo = tarsusServo;
        this.tarsusLength = tarsusLength;
        this.legMidDegrees = legMidDegrees;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public int getBaseServo() {
        return baseServo;
    }

    public float getBaseLength() {
        return baseLength;
    }

    public int getFemurServo() {
        return femurServo;
    }

    public float getFemurLength() {
        return femurLength;
    }

    public int getTarsusServo() {
        return tarsusServo;
    }

    public float getTarsusLength() {
        return tarsusLength;
    }

    public float getLegMidDegrees() {
        return legMidDegrees;
    }

    @Override
    public String toString() {
        return "Id da perna: " + legNumber + "\n" +
                "Servo da base: " + baseServo + "\n" +
                "Servo do femur: " + femurServo + "\n" +
                "Servo do tarso: " + tarsusServo + "\n";
    }

}
