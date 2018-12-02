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

    private double currentLegDegrees;

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

        //lengthVector = this.tarsus.getFinalVector().subtract(this.base.getOriginVector());
        lengthVector = this.tarsus.getFinalVector();

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

        Log.w("Comprimento da perna em relação a origem da base: " + this.lengthVector.subtract(this.base.getOriginVector()).getSize());

    }

    public double getCurrentLegDegrees() {
        return currentLegDegrees;
    }

    public void setCurrentLegDegrees(double currentLegDegrees) {
        this.currentLegDegrees = currentLegDegrees;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void elevate(double currentHeight, double newHeight, double finalLength, double precision) {

        Log.i("Altura atual: " + currentHeight);
        Log.i("Altura desejada: " + newHeight);

        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double tempxf = 0;
        double tempxt = 0;
        double cxft = 0;
        double tempyf = 0;
        double tempyt = 0;
        double cyf = 0;
        double cyt = 0;
        double cteta = 0;
        while (cyf - cyt < newHeight) {
            cyf = Math.sin(Math.toRadians(cteta / 3)) * wf;
            cyt = Math.cos(Math.toRadians(cteta)) * wt;
            cteta += precision;
        }

        Log.i(String.format("O angulo em graus encontrado para solução foi: " +
                        "%.2f com precisão de %.2f graus. Portanto tetaF = %.2f, tetaT = %.2f, YT = %.2f e YF = %.2f | Altura anterior: %.2f x Altura desejada: %.2f",
                cteta, precision, cteta/3, cteta, cyt, cyf, currentHeight, newHeight));

        tempxf = Math.cos(Math.toRadians(cteta / 3)) * wf;
        tempxt = Math.sin(Math.toRadians(cteta)) * wt;
        cxft = tempxf + tempxt;

        Log.i("Comprimento da perna: " + cxft);

        //this.femur.move(cteta / 3);
        //this.tarsus.move(cteta);

    }

    public void move(boolean elevate, double angle, double finalLength, double precision, int delayMillis, boolean sameDelay, List<Task> servoTaskList, TaskListener taskListener) {

        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double tempxf = 0;
        double tempxt = 0;
        double cxft = 0;
        double cteta = 0;
        while (cxft < finalLength) {
            tempxf = Math.cos(Math.toRadians(cteta / 3)) * wf;
            tempxt = Math.sin(Math.toRadians(cteta)) * wt;
            cxft = tempxf + tempxt;
            if (cxft >= finalLength)
                break;
            else
                cteta += precision;
            if (cteta < this.tarsus.getServo().getServoData().getLimitMin() || cteta > this.tarsus.getServo().getServoData().getLimitMax() ||
                    cteta/3 < (double)this.femur.getServo().getServoData().getLimitMin() || cteta/3 > (double)this.femur.getServo().getServoData().getLimitMax())
                break;
        }

        final double xf = tempxf;
        final double xt = tempxt;

        Log.i(String.format("O angulo em graus encontrado para solução foi: %.2f com precisão de %.2f graus. Portanto tetaF = %.2f, tetaW = %.2f e Xft = %.2f",
                cteta, precision, cteta/3, cteta, cxft));

        TaskGroup taskGroups = elevate ? new TaskGroup(new int[]{1, 4}) : new TaskGroup(new int[]{4});
        
        if (elevate)
            servoTaskList.add(new ServoTask(
                    this.femur.getServo(),
                    40,
                    sameDelay ? delayMillis : delayMillis / 2,
                    null,
                    new FlavorTaskGroup(0, taskGroups)));

        servoTaskList.add(new ServoTask(
                this.base.getServo(),
                (int) angle,
                sameDelay ? delayMillis : delayMillis / 5,
                new TaskListener[]{new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        double x = Math.cos(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * base.getLength();
                        double y = Math.sin(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * base.getLength();
                        double ox = base.getOriginVector().x;
                        double oy = base.getOriginVector().y;
                        base.getFinalVector().set(ox + x, oy + y);
                        currentLegDegrees = currentPos;
                    }
                }},
                new FlavorTaskGroup(elevate ? 1 : 0, taskGroups)));


        servoTaskList.add(new ServoTask(
                this.femur.getServo(),
                (int) (cteta / 3),
                sameDelay ? delayMillis : (int)(delayMillis / 2.5),
                new TaskListener[]{new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        double x = Math.cos(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * xf;
                        double y = Math.sin(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * xf;
                        double ox = femur.getOriginVector().x;
                        double oy = femur.getOriginVector().y;
                        femur.getFinalVector().set(ox + x, oy + y);
                    }
                }},
                new FlavorTaskGroup(elevate ? 1 : 0, taskGroups)));

        servoTaskList.add(new ServoTask(
                this.tarsus.getServo(),
                (int) cteta,
                sameDelay ? delayMillis : (int)(delayMillis / 2.5),
                new TaskListener[]{taskListener, new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        double x = Math.cos(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * xt;
                        double y = Math.sin(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * xt;
                        double ox = tarsus.getOriginVector().x;
                        double oy = tarsus.getOriginVector().y;
                        tarsus.getFinalVector().set(ox + x, oy + y);
                        lengthVector = tarsus.getFinalVector();
                    }
                }},
                new FlavorTaskGroup(elevate ? 1 : 0, taskGroups)));
    }

    public void stay() {
        this.femur.move(femur.getCurrentAngle());
        this.tarsus.move(tarsus.getCurrentAngle());
    }



}
