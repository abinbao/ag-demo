package com.bjtu.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author apple
 *
 */
public class Square {
	
	private Double x1;
	private Double x2;
	private Double y1;
	private Double y2;
	
	private int m2;
	
	private int count; // 区域中点的个数
	
	private double countLa; // 加入拉布拉斯噪声
	
	private double avg;
	
	private List<Square> squareList;
	
	private List<LineX> lineXList;
	
	private List<LineY> lineYList;
	
	private LineX gold_x_line;
	private LineY gold_y_line;
	
	private boolean flag;
	
	private Square square_D1;
	private Square square_D2;	
	
	private double temp;
	private double threlod;
	
	
	
	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getThrelod() {
		return threlod;
	}

	public void setThrelod(double threlod) {
		this.threlod = threlod;
	}

	public Square(Double x1, Double x2, Double y1, Double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.avg = 0;
		this.count = 0;
		this.squareList = new ArrayList<Square>();
		this.lineXList = new ArrayList<LineX>();
		this.lineYList = new ArrayList<LineY>();
		this.flag = false;
	}

	public Square(Double x1, Double x2, Double y1, Double y2, int count, double countLa, double avg,
			List<Square> squareList, List<LineX> lineXList, List<LineY> lineYList, LineX gold_x_line,
			LineY gold_y_line, boolean flag, Square square_D1, Square square_D2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.count = count;
		this.countLa = countLa;
		this.avg = avg;
		this.squareList = squareList;
		this.lineXList = lineXList;
		this.lineYList = lineYList;
		this.gold_x_line = gold_x_line;
		this.gold_y_line = gold_y_line;
		this.flag = flag;
		this.square_D1 = square_D1;
		this.square_D2 = square_D2;
	}

	public Double getX1() {
		return x1;
	}

	public void setX1(Double x1) {
		this.x1 = x1;
	}

	public Double getX2() {
		return x2;
	}

	public void setX2(Double x2) {
		this.x2 = x2;
	}

	public Double getY1() {
		return y1;
	}

	public void setY1(Double y1) {
		this.y1 = y1;
	}

	public Double getY2() {
		return y2;
	}

	public void setY2(Double y2) {
		this.y2 = y2;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getCountLa() {
		return countLa;
	}

	public void setCountLa(double countLa) {
		this.countLa = countLa;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public List<Square> getSquareList() {
		return squareList;
	}

	public void setSquareList(List<Square> squareList) {
		this.squareList = squareList;
	}

	public List<LineX> getLineXList() {
		return lineXList;
	}

	public void setLineXList(List<LineX> lineXList) {
		this.lineXList = lineXList;
	}

	public List<LineY> getLineYList() {
		return lineYList;
	}

	public void setLineYList(List<LineY> lineYList) {
		this.lineYList = lineYList;
	}

	public LineX getGold_x_line() {
		return gold_x_line;
	}

	public void setGold_x_line(LineX gold_x_line) {
		this.gold_x_line = gold_x_line;
	}

	public LineY getGold_y_line() {
		return gold_y_line;
	}

	public void setGold_y_line(LineY gold_y_line) {
		this.gold_y_line = gold_y_line;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Square getSquare_D1() {
		return square_D1;
	}

	public void setSquare_D1(Square square_D1) {
		this.square_D1 = square_D1;
	}

	public Square getSquare_D2() {
		return square_D2;
	}

	public void setSquare_D2(Square square_D2) {
		this.square_D2 = square_D2;
	}

	public int getM2() {
		return m2;
	}

	public void setM2(int m2) {
		this.m2 = m2;
	}

	public String toString() {
		return "[ Square:" + "x1=" + this.x1 + ", x2=" + this.x2 + ", y1=" + this.y1 + ", y2=" + this.y2  + ", count=" + this.count + 
				", SubSquareSize=" + this.squareList.size() + ", flag=" + this.flag + ", 当前H值:" + this.temp + ", 阈值为：" + this.threlod +" ]"; 
	}
}
