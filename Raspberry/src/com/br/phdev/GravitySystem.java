package com.br.phdev;

import com.br.phdev.members.Body;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class GravitySystem {

    private double width;
    private double height;
    private Vector2D center;

    private GravityCell leftGravityCell;
    private GravityCell rightGravityCell;

    public GravitySystem(Body body, double width, double height) {
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
            double cw = top.leg.getLengthVector().x - top.vertex.x;
            double ch = top.leg.getLengthVector().y - top.vertex.y;
            double sin = ch / top.vertex.subtract(top.leg.getLengthVector()).getSize();
            double degrees = Math.toDegrees(Math.asin(sin));

            System.out.println("Angulo encontrado: " + degrees);
            System.out.println("Angulo a ser aplicado: " + (degrees - 45));
            System.out.println();
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
