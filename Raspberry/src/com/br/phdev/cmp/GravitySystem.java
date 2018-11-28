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

    public GravitySystem(ServoTaskController servoTaskController, Body body, double width, double height, double precision) {
        this.servoTaskController = servoTaskController;
        this.precision = precision;
        this.width = width;
        this.height = height;
        double cx = body.getArea().x / 2;
        double cy = body.getArea().y / 2;
        this.center = new Vector2D(cx, cy);

        this.leftGravityCell = new GravityCell(
                new Vertex(new Vector2D(cx - width/2, cy + height / 2), body.getLeg(Body.LEG_FRONT_LEFT)),
                new Vertex(new Vector2D(cx + width / 2, cy), body.getLeg(Body.LEG_MID_RIGHT)),
                new Vertex(new Vector2D(cx - width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_LEFT))
        );

        this.rightGravityCell = new GravityCell(
                new Vertex(new Vector2D(cx + width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_RIGHT)),
                new Vertex(new Vector2D(cx - width / 2, cy), body.getLeg(Body.LEG_MID_LEFT)),
                new Vertex(new Vector2D(cx + width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_RIGHT))
        );

        Log.w("Centro de gravidade em (" + cx + "," + cy + ")");
        Log.w("Celula esquerda: \n" + this.leftGravityCell.toString());
        Log.w("Celula direita: \n" + this.rightGravityCell.toString());

        init();
    }

    private void waitFor() {
        try {
            movingLeg.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void showVectorInfo(Vertex vertex) {

    }

    public void init() {
        leftGravityCell.initCell();
        rightGravityCell.initCell();
    }

    private class GravityCell implements TaskListener {

        boolean moving;
        boolean active;

        Vertex top;
        Vertex mid;
        Vertex bottom;

        GravityCell(Vertex top, Vertex mid, Vertex bottom) {
            this.top = top;
            this.mid = mid;
            this.bottom = bottom;
        }

        void initCell() {

            List<Task> servoTaskList = new ArrayList<>();
            double cw;
            double ch;
            double hip;
            double sin;
            double degrees;
            double angle;

            TaskListener taskListener = currentPos -> {
                lock.lock();
                movingLeg.signal();
                lock.unlock();
            };

            cw = top.vertex.x - top.leg.getOriginVector().x;
            ch = top.vertex.y - top.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));
            angle = degrees < 45 ? degrees - 45 : 45 - degrees;

            System.out.println("1) TOP VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + angle);
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize() + top.leg.getBase().getLength()));
            System.out.println();

            Log.w("Antigos vetores TOP ( " + top.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    top.leg.getBase().getOriginVector().x,
                    top.leg.getBase().getOriginVector().y,
                    top.leg.getFemur().getOriginVector().x,
                    top.leg.getFemur().getOriginVector().y,
                    top.leg.getTarsus().getOriginVector().x,
                    top.leg.getTarsus().getOriginVector().y
                    ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    top.leg.getBase().getFinalVector().x,
                    top.leg.getBase().getFinalVector().y,
                    top.leg.getFemur().getFinalVector().x,
                    top.leg.getFemur().getFinalVector().y,
                    top.leg.getTarsus().getFinalVector().x,
                    top.leg.getTarsus().getFinalVector().y
            ));
            Log.s("Comprimento atual da perna: " + top.leg.getTarsus().getFinalVector().subtract(top.leg.getBase().getOriginVector()).getSize());

            top.leg.move(top.vertex, angle, hip, precision, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);

            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            Log.w("Novos vetores TOP ( " + top.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    top.leg.getBase().getOriginVector().x,
                    top.leg.getBase().getOriginVector().y,
                    top.leg.getFemur().getOriginVector().x,
                    top.leg.getFemur().getOriginVector().y,
                    top.leg.getTarsus().getOriginVector().x,
                    top.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    top.leg.getBase().getFinalVector().x,
                    top.leg.getBase().getFinalVector().y,
                    top.leg.getFemur().getFinalVector().x,
                    top.leg.getFemur().getFinalVector().y,
                    top.leg.getTarsus().getFinalVector().x,
                    top.leg.getTarsus().getFinalVector().y
            ));
            Log.s("compirmento novo da perna: " + top.leg.getTarsus().getFinalVector().subtract(top.leg.getBase().getOriginVector()).getSize());
            System.out.println();

            cw = mid.vertex.x - mid.leg.getOriginVector().x;
            ch = mid.vertex.y - mid.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("2) MID VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + (degrees));
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize() + top.leg.getBase().getLength()));
            System.out.println();

            Log.w("Antigos vetores MID ( " + mid.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    mid.leg.getBase().getOriginVector().x,
                    mid.leg.getBase().getOriginVector().y,
                    mid.leg.getFemur().getOriginVector().x,
                    mid.leg.getFemur().getOriginVector().y,
                    mid.leg.getTarsus().getOriginVector().x,
                    mid.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    mid.leg.getBase().getFinalVector().x,
                    mid.leg.getBase().getFinalVector().y,
                    mid.leg.getFemur().getFinalVector().x,
                    mid.leg.getFemur().getFinalVector().y,
                    mid.leg.getTarsus().getFinalVector().x,
                    mid.leg.getTarsus().getFinalVector().y
            ));
            Log.s("Comprimento atual da perna: " + mid.leg.getTarsus().getFinalVector().subtract(mid.leg.getBase().getOriginVector()).getSize());

            mid.leg.move(mid.vertex, angle, hip, precision, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);
            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            Log.w("Novos vetores MID ( " + mid.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    mid.leg.getBase().getOriginVector().x,
                    mid.leg.getBase().getOriginVector().y,
                    mid.leg.getFemur().getOriginVector().x,
                    mid.leg.getFemur().getOriginVector().y,
                    mid.leg.getTarsus().getOriginVector().x,
                    mid.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    mid.leg.getBase().getFinalVector().x,
                    mid.leg.getBase().getFinalVector().y,
                    mid.leg.getFemur().getFinalVector().x,
                    mid.leg.getFemur().getFinalVector().y,
                    mid.leg.getTarsus().getFinalVector().x,
                    mid.leg.getTarsus().getFinalVector().y
            ));
            Log.s("Comprimento novo da perna: " + mid.leg.getTarsus().getFinalVector().subtract(mid.leg.getBase().getOriginVector()).getSize());
            System.out.println();

            cw = bottom.vertex.x - bottom.leg.getOriginVector().x;
            ch = bottom.vertex.y - bottom.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin)) * -1;
            angle = degrees >= 45 ? degrees - 45 : 45 - degrees;

            System.out.println("3) BOTTOM VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + angle);
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize() + top.leg.getBase().getLength()));
            System.out.println();

            Log.w("Antigos vetores BOTTOM ( " + mid.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    bottom.leg.getBase().getOriginVector().x,
                    bottom.leg.getBase().getOriginVector().y,
                    bottom.leg.getFemur().getOriginVector().x,
                    bottom.leg.getFemur().getOriginVector().y,
                    bottom.leg.getTarsus().getOriginVector().x,
                    bottom.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    bottom.leg.getBase().getFinalVector().x,
                    bottom.leg.getBase().getFinalVector().y,
                    bottom.leg.getFemur().getFinalVector().x,
                    bottom.leg.getFemur().getFinalVector().y,
                    bottom.leg.getTarsus().getFinalVector().x,
                    bottom.leg.getTarsus().getFinalVector().y
            ));
            Log.s("Comprimento atual da perna: " + bottom.leg.getTarsus().getFinalVector().subtract(bottom.leg.getBase().getOriginVector()).getSize());

            bottom.leg.move(bottom.vertex, angle, hip, precision, servoTaskList, taskListener);
            servoTaskController.addTasks(servoTaskList);
            lock.lock();
            waitFor();
            lock.unlock();
            servoTaskList.clear();

            Log.w("Novos vetores BOTTOM ( " + mid.leg.getLegData().getLegNumber() + " ):");
            Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                    bottom.leg.getBase().getOriginVector().x,
                    bottom.leg.getBase().getOriginVector().y,
                    bottom.leg.getFemur().getOriginVector().x,
                    bottom.leg.getFemur().getOriginVector().y,
                    bottom.leg.getTarsus().getOriginVector().x,
                    bottom.leg.getTarsus().getOriginVector().y
            ));
            Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                    bottom.leg.getBase().getFinalVector().x,
                    bottom.leg.getBase().getFinalVector().y,
                    bottom.leg.getFemur().getFinalVector().x,
                    bottom.leg.getFemur().getFinalVector().y,
                    bottom.leg.getTarsus().getFinalVector().x,
                    bottom.leg.getTarsus().getFinalVector().y
            ));
            Log.s("Comprimento novo da perna: " + bottom.leg.getTarsus().getFinalVector().subtract(bottom.leg.getBase().getOriginVector()).getSize());
            System.out.println();


        }

        @Override
        public void onServoTaskComplete(double currentPos) {
            notifyAll();
        }

        @Override
        public String toString() {
            return "Top vertex-> " + top.toString() + "\n" +
                    "Mid vertex-> " + mid.toString() + "\n" +
                    "Bottom vertex-> " + bottom.toString();
        }

    }

    private class Vertex {

        Vector2D vertex;
        Leg leg;

        Vertex(Vector2D vertex, Leg leg) {
            this.vertex = vertex;
            this.leg = leg;
        }

        @Override
        public String toString() {
            return "Leg number: " + leg.getLegData().getLegNumber() + "  V(" + vertex.x + "," + vertex.y + ")";
        }
    }

}
