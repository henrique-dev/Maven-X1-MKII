package com.br.phdev.cmp;

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
    private final Condition movingLeg = lock.newCondition();

    private ServoTaskController servoTaskController;

    private double precision;

    private double width;
    private double height;
    private Vector2D center;

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
        this.center = new Vector2D(cx, cy);

        this.leftGravityCell = new GravityCell(
                new Vertex("Top", new Vector2D(cx - width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_LEFT)),
                new Vertex("Mid", new Vector2D(cx + width / 2, cy), body.getLeg(Body.LEG_MID_RIGHT)),
                new Vertex("Bottom", new Vector2D(cx - width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_LEFT))
        );

        this.rightGravityCell = new GravityCell(
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
        this.center.addMe(vector2D);
    }

    private void init() {
        leftGravityCell.initCell();
        rightGravityCell.initCell();
    }

    private void waitFor() {
        try {
            movingLeg.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private class GravityCell implements TaskListener {

        boolean moving;

        Vertex top;
        Vertex mid;
        Vertex bottom;

        GravityCell(Vertex top, Vertex mid, Vertex bottom) {
            this.top = top;
            this.mid = mid;
            this.bottom = bottom;
        }

        void initCell() {


            top.init();
            mid.init();
            bottom.init();

            /*
            List<Task> servoTaskList = new ArrayList<>();
            double cw;
            double ch;
            double hip;
            double sin;
            double degrees;
            double angle;

            cw = top.vertex.x - top.leg.getOriginVector().x;
            ch = top.vertex.y - top.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));
            if (top.vertex.angleSign(top.leg.getTarsus().getFinalVector()) > 0)
                Log.e("SUBINDO " + (45 - module(degrees)));
            else
                Log.e("DESCENDO " + (45 - module(degrees)));

            angle = degrees < 45 ? degrees - 45 : 45 - degrees;

            Log.m(String.format("1) TOP VERTEX > Angulo encontrado: %.2f  |  Angulo a ser aplicado: %.2f  |  Comprimento esperado para a perna: %.2f",
                    degrees, angle,
                    new Vector2D(cw, ch).getSize() + top.leg.getBase().getLength()));

            showVertexrInfo("Antigos vetores TOP", top);
            Log.s("Comprimento atual da perna: " + top.leg.getTarsus().getFinalVector().subtract(top.leg.getBase().getOriginVector()).getSize());

            top.leg.move(true, angle, hip, precision, gaitSpeed, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);

            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            showVertexrInfo("Novos vetores TOP", top);
            Log.s("Comprimento novo da perna: " + top.leg.getTarsus().getFinalVector().subtract(top.leg.getBase().getOriginVector()).getSize());
            System.out.println();

            cw = mid.vertex.x - mid.leg.getOriginVector().x;
            ch = mid.vertex.y - mid.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            angle = degrees = Math.toDegrees(Math.asin(sin));
            System.out.println(module(angle));
            System.out.println(module(degrees));
            if (mid.vertex.angleSign(mid.leg.getTarsus().getFinalVector()) > 0)
                Log.e("SUBINDO " + (45 - module(degrees)));
            else
                Log.e("DESCENDO " + (45 - module(degrees)));

            Log.m(String.format("2) MID VERTEX > Angulo encontrado: %.2f  |  Angulo a ser aplicado: %.2f  |  Comprimento esperado para a perna: %.2f",
                    degrees, angle,
                    new Vector2D(cw, ch).getSize() + mid.leg.getBase().getLength()));

            showVertexrInfo("Antigos vetores MID", mid);
            Log.s("Comprimento atual da perna: " + mid.leg.getTarsus().getFinalVector().subtract(mid.leg.getBase().getOriginVector()).getSize());

            mid.leg.move(true, angle, hip, precision, gaitSpeed, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);
            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            showVertexrInfo("Novos vetores MID", mid);
            Log.s("Comprimento novo da perna: " + mid.leg.getTarsus().getFinalVector().subtract(mid.leg.getBase().getOriginVector()).getSize());
            System.out.println();

            cw = bottom.vertex.x - bottom.leg.getOriginVector().x;
            ch = bottom.vertex.y - bottom.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;

            degrees = Math.toDegrees(Math.asin(sin)) * -1;
            angle = degrees >= 45 ? degrees - 45 : 45 - degrees;
            if (bottom.vertex.angleSign(bottom.leg.getTarsus().getFinalVector()) > 0)
                Log.e("SUBINDO " + (45 - module(degrees)));
            else
                Log.e("DESCENDO " + (45 - module(degrees)));

            Log.m(String.format("3) BOTTOM VERTEX > Angulo encontrado: %.2f  |  Angulo a ser aplicado: %.2f  |  Comprimento esperado para a perna: %.2f",
                    degrees, angle,
                    new Vector2D(cw, ch).getSize() + bottom.leg.getBase().getLength()));

            showVertexrInfo("Antigos vetores MID", bottom);
            Log.s("Comprimento atual da perna: " + bottom.leg.getTarsus().getFinalVector().subtract(bottom.leg.getBase().getOriginVector()).getSize());

            bottom.leg.move(true, angle, hip, precision, gaitSpeed, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);
            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            showVertexrInfo("Novos vetores MID", bottom);
            Log.s("Comprimento novo da perna: " + bottom.leg.getTarsus().getFinalVector().subtract(bottom.leg.getBase().getOriginVector()).getSize());
            System.out.println();

            this.moving = false;*/
        }

        private void adjust(Vector2D vector2D) {

        }

        private double module(double value) {
            if (value < 0)
                value *= -1;
            return value;
        }

        @Override
        public void onServoTaskComplete(double currentPos) {
            notifyAll();
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
            double vw;
            double vh;
            double lw;
            double lh;
            double vhip;
            double lhip;
            double vsin;
            double lsin;
            double vdegrees;
            double ldegrees;
            double angle;

            vw = vertex.x - leg.getOriginVector().x;
            vh = vertex.y - leg.getOriginVector().y;
            vhip = Math.sqrt(Math.pow(vw, 2) + Math.pow(vh, 2));
            vsin = vh / vhip;
            vdegrees = Math.toDegrees(Math.asin(vsin));

            lw = leg.getLengthVector().x - leg.getOriginVector().x;
            lh = leg.getLengthVector().y - leg.getOriginVector().y;
            lhip = Math.sqrt(Math.pow(lw, 2) + Math.pow(lh, 2));
            lsin = lh / lhip;
            ldegrees = Math.toDegrees(Math.asin(lsin));

            angle = ldegrees - vdegrees;

            Log.m(String.format("1) " + name + " VERTEX > Angulo encontrado: %.2f  |  Angulo a ser aplicado: %.2f  |  Comprimento esperado para a perna: %.2f",
                    vdegrees, angle,
                    new Vector2D(vw, vh).getSize() + leg.getBase().getLength()));

            showVertexrInfo("Antigos vetores " + name, this);
            Log.s("Comprimento atual da perna: " + leg.getTarsus().getFinalVector().subtract(leg.getBase().getOriginVector()).getSize());

            leg.move(true, angle, vhip, precision, gaitSpeed, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);

            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            showVertexrInfo("Novos vetores " + name, this);
            Log.s("Comprimento novo da perna: " + leg.getTarsus().getFinalVector().subtract(leg.getBase().getOriginVector()).getSize());
            System.out.println();
        }

        void adjust() {

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

}
