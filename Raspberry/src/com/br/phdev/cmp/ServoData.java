package com.br.phdev.cmp;

public class ServoData {

    private final String moduleAddress;
    private int globalChannel;
    private int localChannel;
    private int minPosition;
    private int midPosition;
    private int maxPosition;

    public ServoData(String moduleAddress, int globalChannel, int localChannel, int minPosition, int midPosition, int maxPosition) {
        this.moduleAddress = moduleAddress;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
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

    @Override
    public String toString() {
        return "Modulo pertencente: " + moduleAddress + "\n" +
                "Canal local: " + localChannel + "\n" +
                "Canal global: " + globalChannel + "\n" +
                "Posição minima: pertencente: " + minPosition + "\n" +
                "Posição média: pertencente: " + midPosition + "\n" +
                "Posição máxima: pertencente: " + maxPosition;
    }

}
