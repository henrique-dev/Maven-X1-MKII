package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;

public class ServosController {

    private PCA9685 module1;
    private PCA9685 module2;

    public ServosController(PCA9685 module1, PCA9685 module2) {
        this.module1 = module1;
        this.module2 = module2;
    }

    private void setServoPosition(int servoGlobalChannel, int position) {
        switch (servoGlobalChannel) {
            case 0:
                //module1.setPWM(1, );
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
        }
    }

}
