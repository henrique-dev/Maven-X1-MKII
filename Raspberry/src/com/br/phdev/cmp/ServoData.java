package com.br.phdev.cmp;

public class ServoData {

    private String moduleAddress;
    private int globalChannel;
    private int localChannel;
    private float minPosition; // -90° position
    private float midPosition; // 0° position
    private float maxPosition; // 90° position
    private int limitMin;
    private int limitMax;
    private int degreesOpening;
    private float step;

    public ServoData(String moduleAddress, int globalChannel, int localChannel, float minPosition, float midPosition, float maxPosition, int limitMin, int limitMax, int degreesOpening) {
        this.moduleAddress = moduleAddress;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
        this.limitMin = limitMin;
        this.limitMax = limitMax;
        this.degreesOpening = degreesOpening;
        if (degreesOpening != 0)
            this.step = (maxPosition - minPosition) / (float)degreesOpening;
        else
            this.step = 0;
    }

    public String getModuleAddress() {
        return moduleAddress;
    }

    public void setModuleAddress(String moduleAddress) {
        this.moduleAddress = moduleAddress;
    }

    public int getGlobalChannel() {
        return globalChannel;
    }

    public void setGlobalChannel(int globalChannel) {
        this.globalChannel = globalChannel;
    }

    public int getLocalChannel() {
        return localChannel;
    }

    public void setLocalChannel(int localChannel) {
        this.localChannel = localChannel;
    }

    public float getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(float minPosition) {
        this.minPosition = minPosition;
    }

    public float getMidPosition() {
        return midPosition;
    }

    public void setMidPosition(float midPosition) {
        this.midPosition = midPosition;
    }

    public float getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(float maxPosition) {
        this.maxPosition = maxPosition;
    }

    public int getLimitMin() {
        return limitMin;
    }

    public void setLimitMin(int limitMin) {
        this.limitMin = limitMin;
    }

    public int getLimitMax() {
        return limitMax;
    }

    public void setLimitMax(int limitMax) {
        this.limitMax = limitMax;
    }

    public int getDegreesOpening() {
        return degreesOpening;
    }

    public void setDegreesOpening(int degreesOpening) {
        this.degreesOpening = degreesOpening;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "\nModulo pertencente: " + this.moduleAddress + "\n" +
                "Canal local: " + this.localChannel + "\n" +
                "Canal global: " + this.globalChannel + "\n" +
                "Posição minima: " + this.minPosition + "\n" +
                "Posição média: " + this.midPosition + "\n" +
                "Posição máxima: " + this.maxPosition + "\n" +
                "Abertura total do servo: " + this.degreesOpening + "\n" +
                "Limite máximo de abertura: " + this.limitMax + "\n" +
                "Limite mínimo de abertura: " + this.limitMin + "\n" +
                "Passo: " + this.step + "\n";
    }

}
