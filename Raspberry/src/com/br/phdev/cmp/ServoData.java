package com.br.phdev.cmp;

public class ServoData {

    private final String moduleAddress;
    private int globalChannel;
    private int localChannel;
    private int minPosition; // -90°
    private int midPosition; // 0°
    private int maxPosition; // 90°
    private int limitMin;
    private int limitMax;
    private float step;

    public ServoData(String moduleAddress, int globalChannel, int localChannel, int minPosition, int midPosition, int maxPosition, int limitMin, int limitMax) {
        this.moduleAddress = moduleAddress;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
        this.limitMin = limitMin;
        this.limitMax = limitMax;
        this.step = ((float)maxPosition - (float)minPosition) / 180;
    }

    public String getModuleAddress() {
        return moduleAddress;
    }

    public int getGlobalChannel() {
        return globalChannel;
    }

    public int getLocalChannel() {
        return localChannel;
    }

    public int getMinPosition() {
        return minPosition;
    }

    public int getMidPosition() {
        return midPosition;
    }

    public int getMaxPosition() {
        return maxPosition;
    }

    public int getLimitMin() {
        return limitMin;
    }

    public int getLimitMax() {
        return limitMax;
    }

    public void setGlobalChannel(int globalChannel) {
        this.globalChannel = globalChannel;
    }

    public void setLocalChannel(int localChannel) {
        this.localChannel = localChannel;
    }

    public void setMinPosition(int minPosition) {
        this.minPosition = minPosition;
    }

    public void setMidPosition(int midPosition) {
        this.midPosition = midPosition;
    }

    public void setMaxPosition(int maxPosition) {
        this.maxPosition = maxPosition;
    }

    public void setLimitMin(int limitMin) {
        this.limitMin = limitMin;
    }

    public void setLimitMax(int limitMax) {
        this.limitMax = limitMax;
    }

    public float getStep() {
        return step;
    }

    @Override
    public String toString() {
        return "Modulo pertencente: " + moduleAddress + "\n" +
                "Canal local: " + localChannel + "\n" +
                "Canal global: " + globalChannel + "\n" +
                "Posição minima: pertencente: " + minPosition + "\n" +
                "Posição média: pertencente: " + midPosition + "\n" +
                "Posição máxima: pertencente: " + maxPosition + "\n" +
                "Passo: " + step ;
    }

}
