package com.br.phdev.cmp;

public class Timer {

    private long startTicks;
    private long pausedTicks;
    private boolean started;
    private boolean paused;

    public Timer() {
        this.startTicks = 0;
        this.pausedTicks = 0;
        this.paused = false;
        this.started = false;
    }

    public Timer start() {
        this.started = true;
        this.paused = false;
        this.startTicks = System.nanoTime();
        this.pausedTicks = 0;
        return this;
    }

    public void stop() {
        this.started = false;
        this.paused = false;
        this.startTicks = 0;
        this.pausedTicks = 0;
    }

    public void pause() {
        if (this.started && !this.paused) {
            this.paused = true;
            this.pausedTicks = System.nanoTime() - this.startTicks;
            this.startTicks = 0;
        }
    }

    public void unPause() {
        if (this.started && this.paused) {
            this.paused = false;
            this.startTicks = System.nanoTime() - this.startTicks;
            this.pausedTicks = 0;
        }
    }

    public long getTicksInNanoSeconds() {
        long time = 0;
        if (this.started) {
            if (this.paused) {
                time = this.pausedTicks;
            } else {
                time = System.nanoTime() - this.startTicks;
            }
        }
        return time;
    }

    public long getTicksInMilliSeconds() {
        long time = 0;
        if (this.started) {
            if (this.paused) {
                time = this.pausedTicks;
            } else {
                time = System.nanoTime() - this.startTicks;
            }
        }
        return time / 1000000;
    }

    public int getTicksInSeconds() {
        long time = 0;
        if (this.started) {
            if (this.paused) {
                time = this.pausedTicks;
            } else {
                time = System.nanoTime() - this.startTicks;
            }
        }
        return (int)time / 1000000000;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return paused;
    }

}
