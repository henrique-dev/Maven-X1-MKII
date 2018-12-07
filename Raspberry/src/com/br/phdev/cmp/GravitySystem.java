package com.br.phdev.cmp;

import com.br.phdev.cmp.gravitysystem.GravityCell;
import com.br.phdev.cmp.gravitysystem.Vertex;
import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskListener;
import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class GravitySystem  {

    private final Lock lock = new ReentrantLock();
    private final Condition movingLeg = lock.newCondition();
    private final Condition movingCell = lock.newCondition();

    private ServoTaskController servoTaskController;

    private double precision;

    private double width;
    private double height;

    private GravityCell leftGravityCell;
    private GravityCell rightGravityCell;

    private int gaitSpeed;

    private Body body;

    public GravitySystem(ServoTaskController servoTaskController, Body body, double width, double height, double precision, int gaitSpeed) {
        this.servoTaskController = servoTaskController;
        this.precision = precision;
        this.gaitSpeed = gaitSpeed;
        this.width = width;
        this.height = height;
        this.body = body;
        double cx = body.getArea().x / 2;
        double cy = body.getArea().y / 2;

        this.leftGravityCell = new GravityCell(
                new Vector2D(cx, cy),
                new Vertex("Top", new Vector2D(cx - width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_LEFT), precision, gaitSpeed),
                new Vertex("Mid", new Vector2D(cx + width / 2, cy), body.getLeg(Body.LEG_MID_RIGHT), precision, gaitSpeed),
                new Vertex("Bottom", new Vector2D(cx - width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_LEFT), precision, gaitSpeed)
        );

        this.rightGravityCell = new GravityCell(
                new Vector2D(cx, cy),
                new Vertex("Top", new Vector2D(cx + width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_RIGHT), precision, gaitSpeed),
                new Vertex("Mid", new Vector2D(cx - width / 2, cy), body.getLeg(Body.LEG_MID_LEFT), precision, gaitSpeed),
                new Vertex("Bottom", new Vector2D(cx + width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_RIGHT), precision, gaitSpeed)
        );

        Log.s("Centro de gravidade em (" + cx + "," + cy + ")");
        Log.w("Celula esquerda: \n" + this.leftGravityCell.toString());
        Log.w("Celula direita: \n" + this.rightGravityCell.toString());
    }

    void elevate(int nextHeight) {
        List<Task> taskList = new ArrayList<>();
        if (leftGravityCell.elevate(nextHeight, taskList, null))
            if (rightGravityCell.elevate(nextHeight, taskList, waitingTaskCellListener)) {
                servoTaskController.addTasks(taskList);
                waitForAnotherCell();
                taskList.clear();
            } else {
                Log.e("Movimento invalido");
            } else {
            Log.e("Movimento invalido");
        }
        leftGravityCell.stabilize();
        rightGravityCell.stabilize();
    }

    void rotate(double angle) {
        List<Task> taskList = new ArrayList<>();
        leftGravityCell.rotate(angle);
        if (leftGravityCell.adjustLegToVertex(new Vector2D(0,0), true, gaitSpeed, false, taskList, waitingTaskCellListener)) {
            servoTaskController.addTasks(taskList);
            waitForAnotherCell();
            taskList.clear();
        } else
            Log.e("Movimento invalido");
        leftGravityCell.stabilize();

        rightGravityCell.rotate(angle);
        if (rightGravityCell.adjustLegToVertex(new Vector2D(0,0), true, gaitSpeed, false, taskList, waitingTaskCellListener)) {
            servoTaskController.addTasks(taskList);
            waitForAnotherCell();
            taskList.clear();
        } else
            Log.e("Movimento invalido");
        rightGravityCell.stabilize();

        leftGravityCell.rotateBodyToVertex(angle, taskList, null);
        rightGravityCell.rotateBodyToVertex(-angle, taskList, waitingTaskCellListener);
        servoTaskController.addTasks(taskList);
        waitForAnotherCell();

    }


    void adjustGravitySystem(double width, double height, double precision, int gaitSpeed) {
        this.precision = precision;
        this.gaitSpeed = gaitSpeed;
        this.width = width;
        this.height = height;

        double cx = leftGravityCell.getCenter().x;
        double cy = leftGravityCell.getCenter().y;

        this.leftGravityCell.getCenter().x += cx - this.leftGravityCell.getCenter().x;
        this.leftGravityCell.getCenter().y += cy - this.leftGravityCell.getCenter().y;
        this.leftGravityCell.getTop().getVertex().x = cx - width / 2;
        this.leftGravityCell.getTop().getVertex().y = cy + height / 2;
        this.leftGravityCell.getMid().getVertex().x = cx + width / 2;
        this.leftGravityCell.getMid().getVertex().y = cy;
        this.leftGravityCell.getBottom().getVertex().x = cx - width / 2;
        this.leftGravityCell.getBottom().getVertex().y = cy - height / 2;

        cx = rightGravityCell.getCenter().x;
        cy = rightGravityCell.getCenter().y;

        this.rightGravityCell.getCenter().x += cx - this.rightGravityCell.getCenter().x;
        this.rightGravityCell.getCenter().y += cy - this.rightGravityCell.getCenter().y;
        this.rightGravityCell.getTop().getVertex().x = cx + width / 2;
        this.rightGravityCell.getTop().getVertex().y = cy + height / 2;
        this.rightGravityCell.getMid().getVertex().x = cx - width / 2;
        this.rightGravityCell.getMid().getVertex().y = cy;
        this.rightGravityCell.getBottom().getVertex().x = cx + width / 2;
        this.rightGravityCell.getBottom().getVertex().y = cy - height / 2;

        List<Task> taskList = new ArrayList<>();
        if (this.leftGravityCell.adjust(taskList, waitingTaskCellListener)) {
            servoTaskController.addTasks(taskList);
            waitForAnotherCell();
            taskList.clear();
            this.leftGravityCell.stabilize();
            if (this.rightGravityCell.adjust(taskList, waitingTaskCellListener)) {
                servoTaskController.addTasks(taskList);
                waitForAnotherCell();
                taskList.clear();
                this.rightGravityCell.stabilize();
                Log.s("Centro de gravidade em (" + cx + "," + cy + ")");
                Log.w("Celula esquerda: \n" + this.leftGravityCell.toString());
                Log.w("Celula direita: \n" + this.rightGravityCell.toString());
            }
        }
    }

    void move(Vector2D vector2D, int stepAmount, int gaitSpeed) {
        Scanner sc = new Scanner(System.in);
        List<Task> taskList = new ArrayList<>();
        for (int i=0; i<stepAmount; i++) {
            if (leftGravityCell.adjustLegToVertex(vector2D, true, gaitSpeed, false, taskList, waitingTaskCellListener)) {
                servoTaskController.addTasks(taskList);
                waitForAnotherCell();
                taskList.clear();
            } else
                Log.e("Movimento invalido");
            leftGravityCell.stabilize();

            if (leftGravityCell.adjustBodyToVertex(vector2D, gaitSpeed / 5, taskList, null))
                if (rightGravityCell.adjustBodyToVertex(vector2D, gaitSpeed / 5, taskList, waitingTaskCellListener)) {
                    servoTaskController.addTasks(taskList);
                    waitForAnotherCell();
                    taskList.clear();
                } else {
                    Log.e("Movimento invalido");
                } else {
                Log.e("Movimento invalido");
            }
            leftGravityCell.stabilize();
            rightGravityCell.stabilize();

            if (rightGravityCell.adjustLegToVertex(vector2D, true, gaitSpeed, false, taskList, waitingTaskCellListener)) {
                servoTaskController.addTasks(taskList);
                waitForAnotherCell();
                taskList.clear();
            } else
                Log.e("Movimento invalido");

            rightGravityCell.stabilize();
        }
    }

    private void waitForAnotherCell() {
        try {
            lock.lock();
            movingCell.await();
            lock.unlock();
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

    private TaskListener taskListener = currentPos -> {
        lock.lock();
        movingLeg.signal();
        lock.unlock();
    };

    private TaskListener waitingTaskCellListener = new TaskListener() {
        @Override
        public void onServoTaskComplete(double currentPos) {
            lock.lock();
            movingCell.signal();
            lock.unlock();
        }
    };

    class AccGirThread extends Thread {

        @Override
        public void run() {

        }

    }

}
