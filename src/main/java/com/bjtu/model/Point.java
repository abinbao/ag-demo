package com.bjtu.model;

/**
 * @author apple
 */
public class Point {

    public static final int MARK_NULL = 0;
    public static final int MARK_STRONG = 0;
    public static final int MARK_WEAK = 0;

    private Double x;
    private Double y;
    private int mark = MARK_NULL;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String toString() {
        return "[ Point:" + "x=" + this.x + " ," + "y=" + this.y + " ]";
    }

}
