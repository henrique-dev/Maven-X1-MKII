package com.br.phdev.cmp;

public class ScriptCommand {

    public enum ScriptPos {
        UP, MID, DOWN
    }

    private final int servoNum;
    private final long delay;
    private final ScriptPos scriptPos;
    private final int scriptGroup;

    public ScriptCommand(int servoNum, long delay, ScriptPos scriptPos, int scriptGroup) {
        this.servoNum = servoNum;
        this.delay = delay;
        this.scriptPos = scriptPos;
        this.scriptGroup = scriptGroup;
    }

    public int getServoNum() {
        return servoNum;
    }

    public long getDelay() {
        return delay;
    }

    public ScriptPos getScriptPos() {
        return scriptPos;
    }

    public int getScriptGroup() {
        return scriptGroup;
    }

    @Override
    public String toString() {
        return "\nMovendo o servo " + this.servoNum + " para " + this.scriptPos + " em um delay de " + this.delay + " milisegundos\n";
    }

}
