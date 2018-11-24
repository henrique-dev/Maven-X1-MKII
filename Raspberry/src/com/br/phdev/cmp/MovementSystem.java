package com.br.phdev.cmp;

import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;

public class MovementSystem implements Motion {

    private Body body;

    public MovementSystem(Body body) {
        this.body = body;
    }

    public void changeHeight(float desiredHeigth) {
        Log.w("MV SYSTEM");
        this.body.moveZ(desiredHeigth);
    }


    @Override
    public void moveX(float x) {

    }

    @Override
    public void moveY(float y) {

    }

    @Override
    public void moveZ(float z) {

    }

    @Override
    public void rotate(float degrees) {

    }
}
