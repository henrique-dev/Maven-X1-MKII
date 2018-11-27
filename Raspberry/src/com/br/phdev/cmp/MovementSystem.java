package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;

public class MovementSystem {

    private ServoTaskController servoTaskController;
    private GravitySystem gravitySystem;
    private Body body;

    MovementSystem(ServoTaskController servoTaskController, Body body) {
        this.servoTaskController = servoTaskController;
        this.body = body;
    }

    public void initGravitySystem(double width, double height, double precision) {
        Log.i("Iniciando sistema de centro de gravidade");
        this.gravitySystem = new GravitySystem(this.servoTaskController, this.body, width, height, precision);
        Log.s("Sistema de centro de gravidade iniciado");
    }

}
