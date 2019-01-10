package com.bjtu.model;

import java.util.Objects;

/**
 * @author apple
 */
public class Point {

    public static final int MARK_NULL = 0;
    public static final int MARK_STRONG = 0;
    public static final int MARK_WEAK = 0;
    // Kmeans 聚类id
    private int clusterID = -1;
    private double x;
    private double y;
    private int mark = MARK_NULL;

    public Point() {

    }

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

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public String toString() {
        return "[ Point:" + "x=" + this.x + " ," + "y=" + this.y + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Point)) {
            return false;
        }
        Point point = (Point) o;
        return x == point.getX() && y == point.getY() && Objects.equals(x, point.getX())
                && Objects.equals(y, point.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
