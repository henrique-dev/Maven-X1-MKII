package com.br.phdev.cmp;

public class ServoData {

    private final char module;
    private final int globalChannel;
    private final int localChannel;
    private final int minPosition;
    private final int midPosition;
    private final int maxPosition;

    public ServoData(char module, int globalChannel, int localChannel, int minPosition, int midPosition, int maxPosition) {
        this.module = module;
        this.globalChannel = globalChannel;
        this.localChannel = localChannel;
        this.minPosition = minPosition;
        this.midPosition = midPosition;
        this.maxPosition = maxPosition;
    }

    public char getModule() {
        return module;
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

}
