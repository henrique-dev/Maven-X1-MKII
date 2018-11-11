package com.br.phdev.cmp;

public class LegData {

    private final int legNumber;
    private final int baseServo;
    private final int femurServo;
    private final int tarsusServo;

    public LegData(int legNumber, int baseServo, int femurServo, int tarsusServo) {
        this.legNumber = legNumber;
        this.baseServo = baseServo;
        this.femurServo = femurServo;
        this.tarsusServo = tarsusServo;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public int getBaseServo() {
        return baseServo;
    }

    public int getFemurServo() {
        return femurServo;
    }

    public int getTarsusServo() {
        return tarsusServo;
    }

    @Override
    public String toString() {
        return "Id da perna: " + legNumber + "\n" +
                "Servo da base: " + baseServo + "\n" +
                "Servo do femur: " + femurServo + "\n" +
                "Servo do tarso: " + tarsusServo + "\n";
    }

}
