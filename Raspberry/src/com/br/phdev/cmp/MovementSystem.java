package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class MovementSystem {

    private ServoTaskController servoTaskController;
    private GravitySystem gravitySystem;
    private Body body;

    MovementSystem(ServoTaskController servoTaskController, Body body, boolean stay) {
        this.servoTaskController = servoTaskController;
        this.body = body;
        if (stay)
            this.body.stay();
    }

    public void startGravitySystem(double width, double height, double precision, int gaitSpeed) {
        Log.i("Iniciando sistema de centro de gravidade");
        this.gravitySystem = new GravitySystem(this.servoTaskController, this.body, width, height, precision, gaitSpeed);
        //this.gravitySystem.elevate(0);
        //adjustGravitySystem(width, height, precision, gaitSpeed);
        //gravitySystem.adjust();
        Log.s("Sistema de centro de gravidade iniciado");
    }

    public void adjustGravitySystem(double width, double height, double precision, int gaitSpeed) {
        this.gravitySystem.adjustGravitySystem(width, height, precision, gaitSpeed);
    }

    public void move(double stepSizeX, double stepSizeY, int stepAmount, int gaitSpeed) {
        this.gravitySystem.move(new Vector2D(stepSizeX, stepSizeY), stepAmount, gaitSpeed);
    }

    public void elevate(int elevateType) {
        this.gravitySystem.elevate(elevateType);
    }

    public void rotate(double angle) {
        this.gravitySystem.rotate(angle);
    }

}
