package com.br.phdev.cmp.gravitysystem;

import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.misc.Vector2D;

import java.util.List;

public class GravityCell {

    private boolean moving;

    private Vector2D center;
    private Vertex top;
    private Vertex mid;
    private Vertex bottom;

    public GravityCell(Vector2D center, Vertex top, Vertex mid, Vertex bottom) {
        this.center = center;
        this.top = top;
        this.mid = mid;
        this.bottom = bottom;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Vector2D getCenter() {
        return center;
    }

    public void setCenter(Vector2D center) {
        this.center = center;
    }

    public Vertex getTop() {
        return top;
    }

    public void setTop(Vertex top) {
        this.top = top;
    }

    public Vertex getMid() {
        return mid;
    }

    public void setMid(Vertex mid) {
        this.mid = mid;
    }

    public Vertex getBottom() {
        return bottom;
    }

    public void setBottom(Vertex bottom) {
        this.bottom = bottom;
    }

    public boolean elevate(int nextHeight, List<Task> taskList, TaskListener tl) {
        if (top.elevate(nextHeight, taskList, null))
            if (mid.elevate(nextHeight, taskList, null))
                if (bottom.elevate(nextHeight, taskList, tl))
                    return true;
        return false;
    }

    public void rotate(double angle) {
        top.getVertex().rotateMe(Math.toRadians(angle));
        mid.getVertex().rotateMe(Math.toRadians(angle));
        bottom.getVertex().rotateMe(Math.toRadians(angle));
        double cx = (top.getVertex().x + mid.getVertex().x) / 2;
        double cy = (top.getVertex().y + bottom.getVertex().y) / 2;
        Vector2D d = center.subtract(new Vector2D(cx, cy));
        top.getVertex().addMe(d);
        mid.getVertex().addMe(d);
        bottom.getVertex().addMe(d);
    }

    public void rotateBodyToVertex(double angle, int gaitSpeed, double precision, List<Task> taskList, TaskListener tl) {
        top.rotateBodyToVertex(-angle, gaitSpeed, precision, taskList, null);
        mid.rotateBodyToVertex(angle, gaitSpeed, precision, taskList, null);
        bottom.rotateBodyToVertex(angle, gaitSpeed, precision, taskList, tl);
    }

    public boolean adjust(int gaitSpeed, double precision, List<Task> taskList, TaskListener tl) {
        if (top.adjust(gaitSpeed, precision, taskList, null))
            if (mid.adjust(gaitSpeed, precision, taskList, null))
                if (bottom.adjust(gaitSpeed, precision, taskList, tl))
                    return true;
        return false;
    }

    public boolean adjustLegToVertex(Vector2D vector2D, boolean elevate, int gaitSpeed, double precision,  boolean sameSpeed, List<Task> taskList, TaskListener tl) {
        if (top.adjustLegToVertex(vector2D, elevate, gaitSpeed, precision, sameSpeed, taskList, null))
            if (mid.adjustLegToVertex(vector2D, elevate, gaitSpeed, precision,  sameSpeed, taskList,null))
                if (bottom.adjustLegToVertex(vector2D, elevate, gaitSpeed, precision, sameSpeed, taskList, tl)) {
                    center.addMe(vector2D);
                    return true;
                }
        return false;
    }

    public boolean adjustBodyToVertex(Vector2D vector2D, int gaitSpeed, double precision, List<Task> servoTaskList, TaskListener tl) {
        if (top.adjustLegToVertex(vector2D, false, gaitSpeed, precision,  true, servoTaskList, null))
            if (mid.adjustLegToVertex(vector2D, false, gaitSpeed, precision,  true, servoTaskList, null))
                if (bottom.adjustLegToVertex(vector2D, false, gaitSpeed, precision, true, servoTaskList, tl))
                    return true;
        return false;
    }

    public void stabilize() {
        top.stabilize();
        mid.stabilize();
        bottom.stabilize();
    }

    @Override
    public String toString() {
        return "T" + top.toString() + "  M" + mid.toString() + "  B" + bottom.toString() + "\n";
    }

}
