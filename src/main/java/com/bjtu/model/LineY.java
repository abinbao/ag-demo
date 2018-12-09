package com.bjtu.model;

/**
 * 
 * @author apple
 *
 */
public class LineY {

	private double x1;
	private double x2;
	private double y1;
	
	public LineY(double x1, double x2, double y1) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}
	
	public String toString() {
		return "[ LineY: " + "x1=" + this.x1 + ", x2=" + this.x2 + ", y1=" + this.y1 + " ]";
	}
	
}
