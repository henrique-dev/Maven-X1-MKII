package com.br.phdev;

import com.br.phdev.members.Body;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;

public class GravitySystem {

    private double width;
    private double height;
    private Vector2D center;

    public GravitySystem(Body body, double width, double height) {
        this.width = width;
        this.height = height;
        double cx = body.getArea().x / 2;
        double cy = body.getArea().y / 2;
        this.center = new Vector2D(cx, cy);
        Log.w("Centro de gravidade em (" + cx + "," + cy + ")");
    }


    public void setVertexLeg(int vertex, Leg leg) {
        switch (vertex) {
            case 0:

        }
    }

    private class GravityCell {



        class Vertex {

            Vector2D vertex;
            Leg leg;

            public Vertex(Vector2D vertex, Leg leg) {
                this.vertex = vertex;
                this.leg = leg;
            }
        }

    }

}
