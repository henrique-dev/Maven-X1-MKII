package com.br.phdev.cmp.gravitysystem;

import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

import java.util.List;

public class Vertex {

    private String name;
    private Vector2D vertex;
    private Leg leg;

    private double precision;
    private int gaitSpeed;

    public Vertex(String name, Vector2D vertex, Leg leg, double precision, int gaitSpeed) {
        this.vertex = vertex;
        this.leg = leg;
        this.name = name;
        this.precision = precision;
        this.gaitSpeed = gaitSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2D getVertex() {
        return vertex;
    }

    public void setVertex(Vector2D vertex) {
        this.vertex = vertex;
    }

    public Leg getLeg() {
        return leg;
    }

    public void setLeg(Leg leg) {
        this.leg = leg;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getGaitSpeed() {
        return gaitSpeed;
    }

    public void setGaitSpeed(int gaitSpeed) {
        this.gaitSpeed = gaitSpeed;
    }

    public boolean elevate(int nextHeight, List<Task> servoTaskList, TaskListener tl) {
        double vw = vertex.x - leg.getOriginVector().x;
        double vh = vertex.y - leg.getOriginVector().y;
        double vhip = Math.sqrt(Math.pow(vw, 2) + Math.pow(vh, 2));
        return leg.elevate(nextHeight, vhip, servoTaskList, tl);
    }

    public void rotate(double angle) {
        vertex.rotateMe(Math.toRadians(angle));
    }

    public void rotateBodyToVertex(double angle, List<Task> servoTaskList, TaskListener tl) {
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
        
        leg.move(false, angle, vhip, precision, gaitSpeed, false, servoTaskList, tl);
    }


    public boolean adjust(List<Task> servoTaskList, TaskListener tl) {
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
        return leg.move(true, angle, vhip, precision, gaitSpeed, false, servoTaskList, tl);
    }

    public boolean adjustLegToVertex(Vector2D vector2D, boolean elevate, int gaitSpeed, boolean sameSpeed, List<Task> taskList, TaskListener tl) {
        if (elevate)
            vertex.addMe(vector2D);
        else
            leg.getOriginVector().addMe(vector2D);

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

        return leg.move(elevate, angle, vhip, precision, gaitSpeed, sameSpeed, taskList, tl);
    }

    private void showVertexrInfo(String vertexName, Vertex vertex) {
        Log.w(vertexName + " ( " + leg.getLegData().getLegNumber() + " ):");
        Log.w(String.format("OB( %.3f , %.3f ) OF( %.3f , %.3f ) OT( %.3f , %.3f )",
                leg.getBase().getOriginVector().x,
                leg.getBase().getOriginVector().y,
                leg.getFemur().getOriginVector().x,
                leg.getFemur().getOriginVector().y,
                leg.getTarsus().getOriginVector().x,
                leg.getTarsus().getOriginVector().y
        ));
        Log.w(String.format("FB( %.3f , %.3f ) FF( %.3f , %.3f ) FT( %.3f , %.3f )",
                leg.getBase().getFinalVector().x,
                leg.getBase().getFinalVector().y,
                leg.getFemur().getFinalVector().x,
                leg.getFemur().getFinalVector().y,
                leg.getTarsus().getFinalVector().x,
                leg.getTarsus().getFinalVector().y
        ));
    }

    void stabilize() {
        leg.stay();
    }

    @Override
    public String toString() {
        return " " + leg.getLegData().getLegNumber() + String.format("(%.2f,%.2f)", vertex.x, vertex.y);
    }

}
