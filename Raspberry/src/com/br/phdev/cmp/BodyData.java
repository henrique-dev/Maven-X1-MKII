package com.br.phdev.cmp;

public class BodyData {

    private final float bodyLength;
    private final float bodyWidth;
    private final float bodyHeight;

    public BodyData(float bodyLength, float bodyWidth, float bodyHeight) {
        this.bodyLength = bodyLength;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
    }

    public float getBodyLength() {
        return bodyLength;
    }

    public float getBodyWidth() {
        return bodyWidth;
    }

    public float getBodyHeight() {
        return bodyHeight;
    }

}
