package com.br.phdev;

import com.br.phdev.driver.PCA9685;

import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        try {
            PCA9685 module1 = new PCA9685("0x40");
            module1.init();
            Scanner in = new Scanner(System.in);
            System.out.print("Informe a frequencia: ");
            int freq = Integer.parseInt(in.nextLine());
            module1.setPWMFreq(freq);
            while (true) {
                System.out.print("Informe a posição");
                int pos = Integer.parseInt(in.nextLine());
                module1.setPWM(2, 0, pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
