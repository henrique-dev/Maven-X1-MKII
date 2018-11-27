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

public class GravitySystem  {

    private ServoTaskController servoTaskController;

    private double precision;

    private double width;
    private double height;
    private Vector2D center;

    private GravityCell leftGravityCell;
    private GravityCell rightGravityCell;

    private boolean lock;

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

    private void waitFor(long howMuch) {
        try {
            synchronized (servoTaskController.getMainThread()) {
                wait(howMuch);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
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
            angle = degrees < 45 ? degrees - 45 : 45 - degrees;

            System.out.println("1) TOP VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + angle);
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            List<Task> servoTaskList = new ArrayList<>();

            //top.leg.move(angle, hip, precision);
            //waitFor(1000);
            top.leg.move(angle, hip, precision, servoTaskList, this);
            servoTaskController.addTasks(servoTaskList);
            waitFor(20000);
            servoTaskList.clear();

            cw = mid.vertex.x - mid.leg.getOriginVector().x;
            ch = mid.vertex.y - mid.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("2) MID VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + (degrees));
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            //mid.leg.move(degrees, hip, precision);
            //waitFor(1000);
            mid.leg.move(angle, hip, precision, servoTaskList, this);
            servoTaskController.addTasks(servoTaskList);
            waitFor(20000);
            servoTaskList.clear();

            cw = bottom.vertex.x - bottom.leg.getOriginVector().x;
            ch = bottom.vertex.y - bottom.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin)) * -1;
            angle = degrees >= 45 ? degrees - 45 : 45 - degrees;

            System.out.println("3) BOTTOM VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + angle);
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            //bottom.leg.move(-45 - degrees, hip, precision);
            //waitFor(1000);
            bottom.leg.move(angle, hip, precision, servoTaskList, this);
            servoTaskController.addTasks(servoTaskList);
            waitFor(20000);
            servoTaskList.clear();


        }

        @Override
        public void onServoTaskComplete(float currentPos) {
            notify();
            Log.i("Tarefas acabaram. Acordando");
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
