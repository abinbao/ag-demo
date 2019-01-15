package com.bjtu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author apple
 */
public class Square {

    /**
     * x1 x2 y1 y2 确定一个区域
     */
    private Double x1;
    private Double x2;
    private Double y1;
    private Double y2;

    /**
     * 区域的临近区域
     */
    private List<Square> adjoinList;

    /**
     * 
     */
    private Integer m2;

    private Integer count; // 区域中点的个数

    private double countLa; // 加入拉布拉斯噪声

    /**
     * 子区域点的平均值
     */
    private double avg;

    /**
     * 子区域集合
     */
    private List<Square> squareList;

    /**
     * x 轴线集合
     */
    private List<LineX> lineXList;
    /**
     * y 轴线集合
     */
    private List<LineY> lineYList;

    /**
     * x 轴黄金分割线
     */
    private LineX gold_x_line;
    /**
     * y 轴黄金分割线
     */
    private LineY gold_y_line;

    /**
     * 是否需要分割
     */
    private boolean flag;

    /**
     * 划分区域 D1
     */
    private Square square_D1;
    /**
     * 划分区域 D2
     */
    private Square square_D2;

    private double temp;
    /**
     * 阈值
     */
    private double threlod;

    /**
     * 使用Canopy算法标签
     */
    private String canopyId;

    /**
     * 所属合并区域ID
     */
    private String mergeId;

    public List<Square> getAdjoinList() {
        return adjoinList;
    }

    public void setAdjoinList(List<Square> adjoinList) {
        this.adjoinList = adjoinList;
    }

    public String getMergeId() {
        return mergeId;
    }

    public void setMergeId(String mergeId) {
        this.mergeId = mergeId;
    }

    public String getCanopyId() {
        return canopyId;
    }

    public void setCanopyId(String canopyId) {
        this.canopyId = canopyId;
    }

    public void setM2(Integer m2) {
        this.m2 = m2;
    }

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
            List<Square> squareList, List<LineX> lineXList, List<LineY> lineYList, LineX gold_x_line, LineY gold_y_line,
            boolean flag, Square square_D1, Square square_D2) {
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
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

    public Integer getM2() {
        return m2;
    }

    public void setM2(int m2) {
        this.m2 = m2;
    }

    public String toString() {
        return "[ Square:" + "x1=" + this.x1 + ", x2=" + this.x2 + ", y1=" + this.y1 + ", y2=" + this.y2 + ", count="
                + this.count + ", SubSquareSize=" + this.squareList.size() + ", flag=" + this.flag + ", 当前H值:"
                + this.temp + ", 阈值为：" + this.threlod + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;// 地址相等
        }

        if (obj == null) {
            return false;// 非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if (obj instanceof Square) {
            Square other = (Square) obj;
            // 需要比较的字段相等，则这两个对象相等
            if (this.x1 == other.getX1().doubleValue() && this.x2 == other.getX2().doubleValue()
                    && this.y1 == other.getY1().doubleValue() && this.y2 == other.getY2().doubleValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        Double tt = x1 + x2 + y1 + y2;
        return tt.hashCode();
    }

    /**
     * 区域面积
     * 
     * @return
     */
    public double getSquare() {

        return ((y2 - y1) * (x2 - x1));
    }
}
