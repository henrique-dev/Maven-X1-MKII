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
    private double currentLegHeight;

    private double normalFemurAngle;
    private double normalTarsusAngle;

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
        this.normalFemurAngle = femur.getCurrentAngle();
        this.normalFemurAngle = tarsus.getCurrentAngle();

        this.currentLegHeight = Math.cos(Math.toRadians(tarsus.getCurrentAngle())) * tarsus.getLength() - Math.sin(Math.toRadians(femur.getCurrentAngle())) * femur.getLength();
    }

    public double getNormalFemurAngle() {
        return normalFemurAngle;
    }

    public void setNormalFemurAngle(double normalFemurAngle) {
        this.normalFemurAngle = normalFemurAngle;
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
        //base.setLength(legData.getBaseLength());
        base.setOriginVector(originVector);
        Vector2D baseXY = Vector2D.createByMagAngle(legData.getBaseLength(), legData.getLegMidDegrees()).addMe(originVector);
        base.setFinalVector(new Vector2D(baseXY.x, baseXY.y));

        //femur.setLength(legData.getFemurLength());
        femur.setOriginVector(base.getFinalVector());
        Vector2D femurXY = Vector2D.createByMagAngle(Math.cos(Math.toRadians(femur.getServo().getCurrentPositionDegrees())) * femur.getLength(),
                legData.getLegMidDegrees()).addMe(base.getFinalVector());
        femur.setFinalVector(new Vector2D(femurXY.x, femurXY.y));

        //tarsus.setLength(legData.getTarsusLength());
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
        return base.getCurrentAngle();
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

    public boolean elevate(int nextHeight, double currentLenght, List<Task> servoTaskList, TaskListener taskListener) {
        double beforeLegLenght = currentLegHeight;
        this.currentLegHeight = nextHeight;
        base.getServo().setRawPosition(0);
        if (!this.move(false, getCurrentLegDegrees(), currentLenght, 0.5, 500, true, servoTaskList, taskListener)) {
            this.currentLegHeight = beforeLegLenght;
            return false;
        }
        return true;
    }

    public void rotate(double angle, int delayMillis, List<Task> servoTaskList, TaskListener taskListener) {
        /*TaskGroup taskGroups = new TaskGroup(new int[1]);
        servoTaskList.add(new ServoTask(
                this.base.getServo(),
                (int) angle,
                delayMillis / 5,
                new TaskListener[]{taskListener, new TaskListener() {
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
                new FlavorTaskGroup(0, taskGroups)));*/
    }

    public boolean move(boolean elevate, double angle, double finalLength, double precision, int delayMillis, boolean sameDelay, List<Task> servoTaskList, TaskListener taskListener) {
        double wf = this.femur.getLength();
        double wt = this.tarsus.getLength();
        double xf = 0;
        double xt = 0;
        double yf = 0;
        double yt = 0;
        double xft = 0;
        double yft = 0;
        double tetaf = 0;
        double tetat = 0;
        boolean firstResultFound = false;
        boolean resultFound = false;

        int resultsFound = 0;
        double currentSumT = 0;
        double currentSumF = 0;

        for (tetaf = femur.getLimitMax(); tetaf >= femur.getLimitMin() && !resultFound; tetaf = tetaf - precision) {
            xf = Math.cos(Math.toRadians(tetaf)) * wf;
            yf = Math.sin(Math.toRadians(tetaf)) * wf;
            for (tetat = tarsus.getLimitMax(); tetat >= tarsus.getLimitMin() && !resultFound; tetat = tetat - precision) {
                xt = Math.sin(Math.toRadians(tetat)) * wt;
                yt = Math.cos(Math.toRadians(tetat)) * wt;
                xft = xf + xt;
                yft = yt - yf;
                if (xft >= finalLength - 5 && xft <= finalLength + 5 && yft >= currentLegHeight - 5 && yft <= currentLegHeight + 5) {
                    firstResultFound = true;
                    currentSumF += tetaf;
                    currentSumT += tetat;
                    resultsFound++;
                } else {
                    if (firstResultFound)
                        resultFound = true;
                }
            }
        }

        final double nxf;
        final double nxt;

        if (resultFound) {
            tetat = currentSumT / resultsFound;
            tetaf = currentSumF / resultsFound;
            xt = Math.sin(Math.toRadians(tetat)) * wt;
            xf = Math.cos(Math.toRadians(tetaf)) * wf;
            nxf = xf;
            nxt = xt;
        } else
            return false;

        Log.i(String.format("tetaF = %.2f e tetaW = %.2f. XFT = %.2f e YFT = %.2f",
                tetaf, tetat, xft, yft));

        Log.s("Novos vetores");
        Log.s("Comprimento novo da perna: " + String.format("%.2f", base.getLength() + xft));
        Log.s("Grau novo da perna: " + angle);
        System.out.println();

        TaskGroup taskGroups = elevate ? new TaskGroup(new int[]{2, (angle == getCurrentLegDegrees() ? 2 : 3)}) : new TaskGroup(new int[]{angle == getCurrentLegDegrees() ? 2 : 3});
        
        if (elevate) {
            double currentElevateAngle = femur.getCurrentAngle() + 40;
            if (currentElevateAngle > femur.getLimitMax())
                currentElevateAngle = femur.getLimitMax();

            servoTaskList.add(new ServoTask(
                    this.femur.getServo(),
                    (int) currentElevateAngle,
                    sameDelay ? delayMillis : delayMillis / 2,
                    null,
                    new FlavorTaskGroup(0, taskGroups), true));
            servoTaskList.add(new ServoTask(
                    this.tarsus.getServo(),
                    (int) -currentElevateAngle / 3,
                    sameDelay ? delayMillis : delayMillis / 2,
                    null,
                    new FlavorTaskGroup(0, taskGroups), true));
        }

        if (angle != getCurrentLegDegrees()) {
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
                    new FlavorTaskGroup(elevate ? 1 : 0, taskGroups), false));
        }

        servoTaskList.add(new ServoTask(
                this.femur.getServo(),
                (int) tetaf,
                sameDelay ? delayMillis : (int)(delayMillis / 2.5),
                new TaskListener[]{new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        double x = Math.cos(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * nxf;
                        double y = Math.sin(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * nxf;
                        double ox = femur.getOriginVector().x;
                        double oy = femur.getOriginVector().y;
                        femur.getFinalVector().set(ox + x, oy + y);
                        //normalFemurAngle = currentPos;
                    }
                }},
                new FlavorTaskGroup(elevate ? 1 : 0, taskGroups), false));

        servoTaskList.add(new ServoTask(
                this.tarsus.getServo(),
                (int) tetat,
                sameDelay ? delayMillis : (int)(delayMillis / 2.5),
                new TaskListener[]{taskListener, new TaskListener() {
                    @Override
                    public void onServoTaskComplete(double currentPos) {
                        double x = Math.cos(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * nxt;
                        double y = Math.sin(Math.toRadians(legData.getLegMidDegrees() - currentPos)) * nxt;
                        double ox = tarsus.getOriginVector().x;
                        double oy = tarsus.getOriginVector().y;
                        tarsus.getFinalVector().set(ox + x, oy + y);
                        lengthVector = tarsus.getFinalVector();
                        //normalTarsusAngle = currentPos;
                    }
                }},
                new FlavorTaskGroup(elevate ? 1 : 0, taskGroups), false));

        return true;
    }

    public void stay() {
        this.femur.move(femur.getCurrentAngle() - 3, true, false);
        this.tarsus.move(tarsus.getCurrentAngle() + 3, true, false);
    }

    private double module(double value) {
        if (value < 0)
            value *= -1;
        return value;
    }



}
