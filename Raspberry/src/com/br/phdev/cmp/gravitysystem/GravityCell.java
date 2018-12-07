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
        top.rotate(angle);
        mid.rotate(angle);
        bottom.rotate(angle);
    }

    public void rotateBodyToVertex(double angle, List<Task> taskList, TaskListener tl) {
        top.rotateBodyToVertex(angle, taskList, tl);
        mid.rotateBodyToVertex(angle, taskList, tl);
        bottom.rotateBodyToVertex(angle, taskList, tl);
    }

    public boolean adjust(List<Task> taskList, TaskListener tl) {
        if (top.adjust(taskList, null))
            if (mid.adjust(taskList, null))
                if (bottom.adjust(taskList, tl))
                    return true;
        return false;
    }

    public boolean adjustLegToVertex(Vector2D vector2D, boolean elevate, int gaitSpeed, boolean sameSpeed, List<Task> taskList, TaskListener tl) {
        if (top.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, taskList, null))
            if (mid.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, taskList,null))
                if (bottom.adjustLegToVertex(vector2D, elevate, gaitSpeed, sameSpeed, taskList, tl)) {
                    center.addMe(vector2D);
                    return true;
                }
        return false;
    }

    public boolean adjustBodyToVertex(Vector2D vector2D, int gaitSpeed, List<Task> servoTaskList, TaskListener tl) {
        if (top.adjustLegToVertex(vector2D, false, gaitSpeed, true, servoTaskList, null))
            if (mid.adjustLegToVertex(vector2D, false, gaitSpeed, true, servoTaskList, null))
                if (bottom.adjustLegToVertex(vector2D, false, gaitSpeed, true, servoTaskList, tl))
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
