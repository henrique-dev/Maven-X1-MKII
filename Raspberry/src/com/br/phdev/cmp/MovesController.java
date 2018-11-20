package com.br.phdev.cmp;

public class MovesController {

    private Leg legs[];
    private float minDegreesToRotate;

    public MovesController(Leg legs[]) {
        this.legs = legs;
        this.minDegreesToRotate = this.legs[0].getBase().getServo().getServoData().getLimitMax();
        for (int i=0; i<this.legs.length; i++) {
            if (this.minDegreesToRotate > this.legs[i].getBase().getServo().getServoData().getLimitMax())
                this.minDegreesToRotate = this.legs[i].getBase().getServo().getServoData().getLimitMax();
            if (this.minDegreesToRotate > this.legs[i].getBase().getServo().getServoData().getLimitMin() * -1)
                this.minDegreesToRotate = this.legs[i].getBase().getServo().getServoData().getLimitMin() * -1;
        }
    }

    public void rotate(float degrees) {
        for (Leg leg : this.legs) {
            leg.getBase().move(degrees);
        }
    }

}
