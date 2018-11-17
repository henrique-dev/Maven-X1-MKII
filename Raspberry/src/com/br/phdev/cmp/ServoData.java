package com.br.phdev.cmp;

public class ServoData {

    private final String moduleAddress;
    private int globalChannel;
    private int localChannel;
    private float minPosition; // -90° position
    private float midPosition; // 0° position
    private float maxPosition; // 90° position
    private int limitMin;
    private int limitMax;
    private float step;

    public ServoData(String moduleAddress, int globalChannel, int localChannel, float minPosition, float midPosition, float maxPosition, int limitMin, int limitMax) {
        this.moduleAddress = moduleAddress;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
        this.limitMin = limitMin;
        this.limitMax = limitMax;
        this.step = (maxPosition - minPosition) / 180.f;
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

    public float getMinPosition() {
        return minPosition;
    }

    public float getMidPosition() {
        return midPosition;
    }

    public float getMaxPosition() {
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

    public void setMinPosition(float minPosition) {
        this.minPosition = minPosition;
    }

    public void setMidPosition(float midPosition) {
        this.midPosition = midPosition;
    }

    public void setMaxPosition(float maxPosition) {
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
