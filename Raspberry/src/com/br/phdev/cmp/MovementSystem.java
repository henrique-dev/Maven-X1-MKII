package com.br.phdev.cmp;

import com.br.phdev.members.Body;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class MovementSystem implements Motion {

    private Body body;

    public MovementSystem(Body body) {
        this.body = body;
    }

    public void changeHeight(float desiredHeigth) {
        this.body.moveZ(desiredHeigth);
    }

    public void centerGravity() {

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

    class CenterGravity {

        Vector2D point1;
        Vector2D point2;
        Vector2D point3;

    }

}
