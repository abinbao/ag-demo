package com.bjtu.model;

/**
 * @author apple
 */
public class LineX {

    private double x1;
    private double y1;

    private double y2;

    public LineX(double x1, double y1, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public String toString() {
        return "[ LineX: " + "x1=" + this.x1 + ", y1=" + this.y1 + ", y2=" + this.y2 + " ]";
    }

}
