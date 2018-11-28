package com.br.phdev.members;

import com.br.phdev.cmp.servo.ServoTask;
import com.br.phdev.cmp.task.FlavorTaskGroup;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskGroup;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.data.LegData;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

import java.util.List;

public class Leg {

    private boolean onGround;

    private final LegData legData;
    private final Base base;
    private final Femur femur;
    private final Tarsus tarsus;

    private Vector2D originVector;
    private Vector2D lengthVector;

    public Leg(LegData legData, Base base, Femur femur, Tarsus tarsus) {
        this.legData = legData;
        this.base = base;
        this.femur = femur;
        this.tarsus = tarsus;
        this.onGround = true;
    }

    public LegData getLegData() {
        return legData;
    }

    public Base getBase() {
        return base;
    }

    public Femur getFemur() {
        return femur;
    }

    public Tarsus getTarsus() {
        return tarsus;
    }

    public Vector2D getOriginVector() {
        return originVector;
    }

    public Vector2D getLengthVector() {
        return lengthVector;
    }

    public void setOriginVector(Vector2D originVector) {
        this.originVector = originVector;
        base.setLength(legData.getBaseLength());
        base.setOriginVector(originVector);
        Vector2D baseXY = Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(originVector);
        base.setFinalVector(new Vector2D(baseXY.x, baseXY.y));

        femur.setLength(legData.getFemurLength());
        femur.setOriginVector(base.getFinalVector());
        Vector2D femurXY = Vector2D.createByMagAngle(Math.cos(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength(),
                legData.getLegMidDegrees()).addMe(base.getFinalVector());
        femur.setFinalVector(new Vector2D(femurXY.x, femurXY.y));

        tarsus.setLength(legData.getTarsusLength());
        tarsus.setOriginVector(femur.getFinalVector());
        Vector2D tarsusXY = Vector2D.createByMagAngle(Math.sin(Math.toRadians(tarsus.getServo().getCurrentPositionDegrees())) * tarsus.getLength(),
                legData.getLegMidDegrees()).addMe(femur.getFinalVector());
        tarsus.setFinalVector(new Vector2D(tarsusXY.x, tarsusXY.y));

        lengthVector = this.tarsus.getFinalVector().subtract(this.base.getOriginVector());

        Log.w("");
        Log.w("Vetores da perna " + legData.getLegNumber() + " com inclinação de " + legData.getLegMidDegrees());
        Log.w("Perna originVector: " + originVector);

        Log.w("Base originVector : " + this.base.getOriginVector());
        Log.w("Base lengthVector : " + this.base.getFinalVector());

        Log.w("Femur originVector: " + this.femur.getOriginVector());
        Log.w("Femur lengthVector: " + this.femur.getFinalVector());

        Log.w("Tarso originVector: " + this.tarsus.getOriginVector());
        Log.w("Tarso lengthVector: " + this.tarsus.getFinalVector());

        Log.w("Perna lengthVector: " + lengthVector);

        Log.w("Comprimento da perna em relação a origem da base: " + this.lengthVector.getSize());

    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void move(Vector2D vertexVector, double angle, double finalLength, double precision, List<Task> servoTaskList, TaskListener taskListener) {

        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double cxft = 0;
        double cteta = this.tarsus.getServo().getServoData().getMinPosition();
        while (cxft < finalLength) {
            cxft = Math.cos(Math.toRadians(cteta / 3)) * wf + Math.sin(Math.toRadians(cteta)) * wt;
            if (cxft >= finalLength)
                break;
            else
                cteta += precision;
            if (cteta < this.tarsus.getServo().getServoData().getLimitMin() || cteta > this.tarsus.getServo().getServoData().getLimitMax() ||
                    cteta/3 < (double)this.femur.getServo().getServoData().getLimitMin() || cteta/3 > (double)this.femur.getServo().getServoData().getLimitMax())
                break;
        }

        System.out.println("O angulo em graus encontrado para solução foi: " + cteta + " com precisão de " + precision + " graus");
        System.out.println("Portanto tetaF = " + cteta/3 + " e tetaW = " + cteta);
        System.out.println();

        TaskGroup taskGroups = new TaskGroup(new int[]{1, 4});

        servoTaskList.add(new ServoTask(this.femur.getServo(), 40, 1000, null, new FlavorTaskGroup(0, taskGroups)));

        servoTaskList.add(new ServoTask(
                this.base.getServo(),
                (int) angle,
                800,
                new TaskListener[]{new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        Log.w("Testando funcionalidade - BASE");
                        Log.i("Vetor antigo:");
                        Log.i("Origin vector: " + base.getOriginVector());
                        Log.i("Final vector: " + base.getFinalVector());
                        double x = Math.cos(Math.toRadians(currentPos)) * base.getLength();
                        double y = Math.sin(Math.toRadians(currentPos)) * base.getLength();
                        base.getFinalVector().set(x, y);
                        Log.i("Vetor novo:");
                        Log.i("Origin vector: " + base.getOriginVector());
                        Log.i("Final vector: " + base.getFinalVector());
                    }
                }},
                new FlavorTaskGroup(1, taskGroups)));


        servoTaskList.add(new ServoTask(
                this.femur.getServo(),
                (int) (cteta / 3),
                800,
                new TaskListener[]{new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        Log.w("Testando funcionalidade - FEMUR");
                        Log.i("Vetor antigo:");
                        Log.i("Origin vector: " + femur.getOriginVector());
                        Log.i("Final vector: " + femur.getFinalVector());
                        double x = Math.cos(Math.toRadians(currentPos)) * femur.getLength();
                        double y = Math.sin(Math.toRadians(currentPos)) * femur.getLength();
                        femur.getFinalVector().set(x, y);
                        Log.i("Vetor novo:");
                        Log.i("Origin vector: " + femur.getOriginVector());
                        Log.i("Final vector: " + femur.getFinalVector());
                    }
                }},
                new FlavorTaskGroup(1, taskGroups)));

        servoTaskList.add(new ServoTask(
                this.tarsus.getServo(),
                (int) cteta,
                800,
                new TaskListener[]{taskListener, new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        Log.w("Testando funcionalidade - TARSO");
                        Log.i("Vetor antigo:");
                        Log.i("Origin vector: " + tarsus.getOriginVector());
                        Log.i("Final vector: " + tarsus.getFinalVector());
                        double x = Math.sin(Math.toRadians(currentPos)) * tarsus.getLength();
                        double y = Math.cos(Math.toRadians(currentPos)) * tarsus.getLength();
                        tarsus.getFinalVector().set(x, y);
                        Log.i("Vetor novo:");
                        Log.i("Origin vector: " + tarsus.getOriginVector());
                        Log.i("Final vector: " + tarsus.getFinalVector());
                    }
                }},
                new FlavorTaskGroup(1, taskGroups)));
    }

/*
    public void move(double angle, double finalLength, double precision) {
        this.base.move(angle);
        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double cxft = 0;
        double cteta = 0;
        while (cxft < finalLength) {
            cxft = Math.cos(Math.toRadians(cteta / 3)) * wf + Math.sin(Math.toRadians(cteta)) * wt;
            if (cxft >= finalLength)
                break;
            else
                cteta += precision;
            if (cteta > 45)
                break;
        }

        System.out.println("O angulo em graus encontrado para solução foi: " + cteta + " com precisão de " + precision + " graus");
        System.out.println("Portanto tetaF = " + cteta/3 + " e tetaW = " + cteta);
        System.out.println();

        this.femur.move(cteta / 3);
        this.tarsus.move(cteta);

    }*/

    public void stay() {
        this.femur.move(femur.getCurrentAngle());
        this.tarsus.move(tarsus.getCurrentAngle());
    }



}
