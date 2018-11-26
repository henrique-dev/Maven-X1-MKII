package com.br.phdev.test;

import java.lang.Math;

public class FindXFT {

    public static void main(String[] args) {

        double xft = 7.5;
        double wf = 4.5;
        double wt = 6;
        double precision = 0.1;
        double cxft = 0;
        double cteta = 0;

        while (cxft < xft) {
            cxft = Math.cos(Math.toRadians(cteta / 3)) * wf + Math.sin(Math.toRadians(cteta)) * wt;
            System.out.println("Teta atual: " + cteta);
            System.out.println("Largura atual: " + cxft);
            if (cxft >= xft)
                break;
            else
                cteta += precision;
            if (cteta >= 45)
                break;
        }

        System.out.println("O angulo em graus encontrado para solução foi: " + cteta + " com precisão de " + precision + " graus");
        System.out.println("Portanto tetaF = " + cteta/3 + " e tetaW = " + cteta);

    }


}
