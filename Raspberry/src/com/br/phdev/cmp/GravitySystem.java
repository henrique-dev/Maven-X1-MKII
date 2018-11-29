package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.ServoTask;
import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.members.Body;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GravitySystem  {

    private final Lock lock = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Condition movingLeg = lock.newCondition();
    private final Condition movingCell = lock2.newCondition();

    private ServoTaskController servoTaskController;

    private double precision;

    private double width;
    private double height;

    private GravityCell leftGravityCell;
    private GravityCell rightGravityCell;

    private int gaitSpeed;

    public GravitySystem(ServoTaskController servoTaskController, Body body, double width, double height, double precision, int gaitSpeed) {
        this.servoTaskController = servoTaskController;
        this.precision = precision;
        this.gaitSpeed = gaitSpeed;
        this.width = width;
        this.height = height;
        double cx = body.getArea().x / 2;
        double cy = body.getArea().y / 2;

        this.leftGravityCell = new GravityCell(
                new Vector2D(cx, cy),
                new Vertex("Top", new Vector2D(cx - width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_LEFT)),
                new Vertex("Mid", new Vector2D(cx + width / 2, cy), body.getLeg(Body.LEG_MID_RIGHT)),
                new Vertex("Bottom", new Vector2D(cx - width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_LEFT))
        );

        this.rightGravityCell = new GravityCell(
                new Vector2D(cx, cy),
                new Vertex("Top", new Vector2D(cx + width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_RIGHT)),
                new Vertex("Mid", new Vector2D(cx - width / 2, cy), body.getLeg(Body.LEG_MID_LEFT)),
                new Vertex("Bottom", new Vector2D(cx + width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_RIGHT))
        );

        Log.s("Centro de gravidade em (" + cx + "," + cy + ")");
        Log.w("Celula esquerda: \n" + this.leftGravityCell.toString());
        Log.w("Celula direita: \n" + this.rightGravityCell.toString());

        init();
    }

    public void adjust(Vector2D vector2D) {
        leftGravityCell.adjustLegToVertex(vector2D, true, 1000, false, waitingTaskCellListener);
        lock2.lock();
        waitForAnotherCell();
        lock2.unlock();
        Log.s("Celula executou o movimento");
        //lock2.lock();
        //leftGravityCell.adjustLegToVertex(vector2D, true, 1000, false, null);
        //rightGravityCell.adjustLegToVertex(vector2D, true, 1000, false, waitingTaskCellListener);
        //waitForAnotherCell();
        //lock2.unlock();
        Log.s("Celula executou o movimento");
    }

    private void init() {
        leftGravityCell.initCell();
        rightGravityCell.initCell();
    }

    private void waitForAnotherCell() {
        try {
            movingCell.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void waitFor() {
        try {
            movingLeg.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private class GravityCell {

        boolean moving;

        Vector2D center;
        Vertex top;
        Vertex mid;
        Vertex bottom;

        public GravityCell(Vector2D center, Vertex top, Vertex mid, Vertex bottom) {
            this.center = center;
            this.top = top;
            this.mid = mid;
            this.bottom = bottom;
        }

        void initCell() {
            top.init();
            mid.init();
            bottom.init();
        }

        private void adjustLegToVertex(Vector2D vector2D, boolean elevate, int gaitSpeed, boolean sameSpeed, TaskListener tl) {
            top.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, tl);
            mid.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, tl);
            bottom.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, tl);
        }

        @Override
        public String toString() {
            return "T" + top.toString() + "  M" + mid.toString() + "  B" + bottom.toString() + "\n";
        }

    }

    private class Vertex {

        String name;
        Vector2D vertex;
        Leg leg;

        Vertex(String name, Vector2D vertex, Leg leg) {
            this.vertex = vertex;
            this.leg = leg;
            this.name = name;
        }

        void init() {
            List<Task> servoTaskList = new ArrayList<>();

            double vw = vertex.x - leg.getOriginVector().x;
            double vh = vertex.y - leg.getOriginVector().y;
            double vhip = Math.sqrt(Math.pow(vw, 2) + Math.pow(vh, 2));
            double vsin = vh / vhip;
            double vdegrees = Math.toDegrees(Math.asin(vsin));

            double lw = leg.getLengthVector().x - leg.getOriginVector().x;
            double lh = leg.getLengthVector().y - leg.getOriginVector().y;
            double lhip = Math.sqrt(Math.pow(lw, 2) + Math.pow(lh, 2));
            double lsin = lh / lhip;
            double ldegrees = Math.toDegrees(Math.asin(lsin));

            double sin = Math.sin(Math.toRadians(leg.getLegData().getLegMidDegrees()));
            double asin = Math.asin(sin);
            double angle = vdegrees - Math.toDegrees(asin);

            Log.m(String.format(name + " VERTEX > Angulo do vertex: %.2f  |  Angulo da perna: %.2f  |  Angulo a ser aplicado: %.2f  |  Comprimento esperado para a perna: %.2f",
                    vdegrees, ldegrees, angle,
                    new Vector2D(vw, vh).getSize() + leg.getBase().getLength()));

            showVertexrInfo("Antigos vetores " + name, this);
            Log.s("Comprimento atual da perna: " + leg.getLengthVector().subtract(leg.getOriginVector()).getSize());
            Log.s("Grau atual da perna: " + leg.getCurrentLegDegrees());

            leg.move(true, angle, vhip, precision, gaitSpeed, false, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);

            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            showVertexrInfo("Novos vetores " + name, this);
            Log.s("Comprimento novo da perna: " + leg.getLengthVector().subtract(leg.getOriginVector()).getSize());
            Log.s("Grau novo da perna: " + leg.getCurrentLegDegrees());
            System.out.println();
        }

        private void adjustLegToVertex(Vector2D vector2D, boolean elevate, int gaitSpeed, boolean sameSpeed, TaskListener tl) {
            List<Task> servoTaskList = new ArrayList<>();

            if (elevate)
                leg.getOriginVector().addMe(vector2D);
            else
                vertex.addMe(vector2D);

            double vw = vertex.x - leg.getOriginVector().x;
            double vh = vertex.y - leg.getOriginVector().y;
            double vhip = Math.sqrt(Math.pow(vw, 2) + Math.pow(vh, 2));
            double vsin = vh / vhip;
            double vdegrees = Math.toDegrees(Math.asin(vsin));

            double lw = leg.getLengthVector().x - leg.getOriginVector().x;
            double lh = leg.getLengthVector().y - leg.getOriginVector().y;
            double lhip = Math.sqrt(Math.pow(lw, 2) + Math.pow(lh, 2));
            double lsin = lh / lhip;
            double ldegrees = Math.toDegrees(Math.asin(lsin));

            double sin = Math.sin(Math.toRadians(leg.getLegData().getLegMidDegrees()));
            double asin = Math.asin(sin);
            double angle = vdegrees - Math.toDegrees(asin);

            leg.move(elevate, angle, vhip, precision, gaitSpeed, sameSpeed, servoTaskList, tl);
            servoTaskController.addTasks(servoTaskList);
        }

        private void showVertexrInfo(String vertexName, Vertex vertex) {
            Log.w(vertexName + " ( " + vertex.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    vertex.leg.getBase().getOriginVector().x,
                    vertex.leg.getBase().getOriginVector().y,
                    vertex.leg.getFemur().getOriginVector().x,
                    vertex.leg.getFemur().getOriginVector().y,
                    vertex.leg.getTarsus().getOriginVector().x,
                    vertex.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    vertex.leg.getBase().getFinalVector().x,
                    vertex.leg.getBase().getFinalVector().y,
                    vertex.leg.getFemur().getFinalVector().x,
                    vertex.leg.getFemur().getFinalVector().y,
                    vertex.leg.getTarsus().getFinalVector().x,
                    vertex.leg.getTarsus().getFinalVector().y
            ));
        }

        @Override
        public String toString() {
            return " " + leg.getLegData().getLegNumber() + String.format("(%.2f,%.2f)", vertex.x, vertex.y);
        }
    }

    private TaskListener taskListener = currentPos -> {
        lock.lock();
        movingLeg.signal();
        lock.unlock();
    };

    private TaskListener waitingTaskCellListener = new TaskListener() {
        @Override
        public void onServoTaskComplete(double currentPos) {
            lock2.lock();
            movingCell.signal();
            lock2.unlock();
        }
    };

}
