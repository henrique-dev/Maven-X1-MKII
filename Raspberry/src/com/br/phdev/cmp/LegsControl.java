package com.br.phdev.cmp;

public class LegsControl {

    private Leg[] legs;
    private Servo[] servos;

    public LegsControl(Leg[] legs, Servo[] servos) {
        this.legs = legs;
        this.servos = servos;
    }

    private class MainThread extends Thread {

        private boolean runningMainLoop;

        @Override
        public void run() {
            this.runningMainLoop = true;
            while (this.runningMainLoop) {

            }
        }

        public boolean isRunningMainLoop() {
            return runningMainLoop;
        }

        public void setRunningMainLoop(boolean runningMainLoop) {
            this.runningMainLoop = runningMainLoop;
        }
    }

}
