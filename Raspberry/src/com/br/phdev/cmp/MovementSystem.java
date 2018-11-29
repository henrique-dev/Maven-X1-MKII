package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class MovementSystem {

    private ServoTaskController servoTaskController;
    private GravitySystem gravitySystem;
    private Body body;

    MovementSystem(ServoTaskController servoTaskController, Body body) {
        this.servoTaskController = servoTaskController;
        this.body = body;
        this.body.stay();
    }

    public void initGravitySystem(double width, double height, double precision, int gaitSpeed) {
        Log.i("Iniciando sistema de centro de gravidade");
        this.gravitySystem = new GravitySystem(this.servoTaskController, this.body, width, height, precision, gaitSpeed);
        Log.s("Sistema de centro de gravidade iniciado");
    }

    public void move(double stepSizeX, double stepSizeY, int stepAmount) {
        this.gravitySystem.adjust(new Vector2D(stepSizeX, stepSizeY), stepAmount);
    }

    public void move(double x, double y, double z) {
        //this.gravitySystem.adjust(new Vector2D(x, y));
    }

}
