package com.br.phdev;

import com.br.phdev.members.Body;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

import java.util.Scanner;

public class GravitySystem {

    private double precision;

    private double width;
    private double height;
    private Vector2D center;

    private GravityCell leftGravityCell;
    private GravityCell rightGravityCell;

    public GravitySystem(Body body, double width, double height, double precision) {
        this.precision = precision;
        this.width = width;
        this.height = height;
        double cx = body.getArea().x / 2;
        double cy = body.getArea().y / 2;
        this.center = new Vector2D(cx, cy);

        this.leftGravityCell = new GravityCell(
                new Vertex(new Vector2D(cx - width/2, cy + height / 2), body.getLeg(Body.LEG_FRONT_LEFT)),
                new Vertex(new Vector2D(cx + width / 2, cy), body.getLeg(Body.LEG_MID_RIGHT)),
                new Vertex(new Vector2D(cx - width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_LEFT))
        );

        this.rightGravityCell = new GravityCell(
                new Vertex(new Vector2D(cx + width / 2, cy + height / 2), body.getLeg(Body.LEG_FRONT_RIGHT)),
                new Vertex(new Vector2D(cx - width / 2, cy), body.getLeg(Body.LEG_MID_LEFT)),
                new Vertex(new Vector2D(cx + width / 2, cy - height / 2), body.getLeg(Body.LEG_BACK_RIGHT))
        );

        Log.w("Centro de gravidade em (" + cx + "," + cy + ")");
        Log.w("Celula esquerda: \n" + this.leftGravityCell.toString());
        Log.w("Celula direita: \n" + this.rightGravityCell.toString());

        reposition();
    }

    public void reposition() {
        leftGravityCell.reposition();
        rightGravityCell.reposition();
    }

    private class GravityCell {

        Vertex top;
        Vertex mid;
        Vertex bottom;

        GravityCell(Vertex top, Vertex mid, Vertex bottom) {
            this.top = top;
            this.mid = mid;
            this.bottom = bottom;
        }

        void reposition() {

            double cw;
            double ch;
            double hip;
            double sin;
            double degrees;

            cw = top.vertex.x - top.leg.getOriginVector().x;
            ch = top.vertex.y - top.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("1) TOP VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + (45 - degrees));
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            top.leg.move(45 - degrees, hip, precision);


            cw = mid.vertex.x - mid.leg.getOriginVector().x;
            ch = mid.vertex.y - mid.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("2) MID VERTEX");
            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + (degrees));
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            mid.leg.move(degrees, hip, precision);


            cw = bottom.vertex.x - bottom.leg.getOriginVector().x;
            ch = bottom.vertex.y - bottom.leg.getOriginVector().y;
            hip = Math.sqrt(Math.pow(cw, 2) + Math.pow(ch, 2));
            sin = ch / hip;
            degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("3) BOTTOM VERTEX");
            System.out.println("Angulo a ser aplicado: " + (-45 - degrees));
            System.out.println("Comprimento esperado para a perna: " + (new Vector2D(cw, ch).getSize()));
            System.out.println();

            bottom.leg.move(-45 - degrees, hip, precision);


        }

        @Override
        public String toString() {
            return "Top vertex-> " + top.toString() + "\n" +
                    "Mid vertex-> " + mid.toString() + "\n" +
                    "Bottom vertex-> " + bottom.toString();
        }

    }

    private class Vertex {

        Vector2D vertex;
        Leg leg;

        Vertex(Vector2D vertex, Leg leg) {
            this.vertex = vertex;
            this.leg = leg;
        }

        @Override
        public String toString() {
            return "Leg number: " + leg.getLegData().getLegNumber() + "  V(" + vertex.x + "," + vertex.y + ")";
        }
    }

}
