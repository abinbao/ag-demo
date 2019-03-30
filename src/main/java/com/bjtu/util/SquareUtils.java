package com.bjtu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.model.Point;
import com.bjtu.model.Square;

/**
 * 区域面积
 * 
 * @author apple
 */
public final class SquareUtils {

    private static Logger logger = LoggerFactory.getLogger(SquareUtils.class);

    /**
     * @param xLength
     *            长，也就是横坐标的长度
     * @param yLength
     *            宽，也就是纵坐标的长度
     * @param unit
     *            单元
     * @return
     */
    public static List<Square> divideSquare(double xLength, double yLength, double unit,
            Map<String, Square> squareMap) {
        List<Square> squareList = new ArrayList();
        double xStart = -(xLength / 2);
        double xEnd = (xLength / 2);
        double yStart = -(yLength / 2);
        double yEnd = (yLength / 2);
        for (double x = xStart; x < xEnd; x = x + unit) {
            double x2 = x + unit;
            if (x + unit > xEnd) {
                x2 = xEnd;
            }
            for (double y = yStart; y < yEnd; y = y + unit) {
                double y2 = y + unit;
                if (y + unit > yEnd) {
                    y2 = yEnd;
                }
                Square square = new Square(x, x2, y, y2);
                StringBuilder sb = new StringBuilder();
                squareMap.put(
                        sb.append(x).append("_").append(x2).append("_").append(y).append("_").append(y2).toString(),
                        square);
                setAdjoinSquare(xLength, yLength, unit, square);
                squareList.add(square);
            }
        }
        return squareList;
    }

    /**
     * 生成区域的相邻区域
     * 
     * @param xLength
     * @param yLength
     * @param unit
     * @param square
     */
    private static void setAdjoinSquare(double xLength, double yLength, double unit, Square square) {
        double xStart = -(xLength / 2);
        double xEnd = (xLength / 2);
        double yStart = -(yLength / 2);
        double yEnd = (yLength / 2);

        double x1 = square.getX1();
        double x2 = square.getX2();
        double y1 = square.getY1();
        double y2 = square.getY2();
        List<Square> list = new ArrayList<>();
        // 先算上边的区域
        if (y2 + unit >= yEnd && yEnd != y2) {
            Square item = new Square(x1, x2, y2, yEnd);
            list.add(item);
        }
        if (y2 + unit < yEnd) {
            Square item = new Square(x1, x2, y2, y2 + unit);
            list.add(item);
        }
        // 下边的相邻区域
        if (y1 - unit <= yStart && yStart != y1) {
            Square item = new Square(x1, x2, yStart, y1);
            list.add(item);
        }
        if (y1 - unit > yStart) {
            Square item = new Square(x1, x2, y1 - unit, y1);
            list.add(item);
        }
        // 右边的区域
        if (x2 + unit >= xEnd && xEnd != x2) {
            Square item = new Square(x2, xEnd, y1, y2);
            list.add(item);
        }
        if (x2 + unit < xEnd) {
            Square item = new Square(x2, x2 + unit, y1, y2);
            list.add(item);
        }
        // 左边的区域
        if (x1 - unit > xStart) {
            Square item = new Square(x1 - unit, x1, y1, y2);
            list.add(item);
        }
        if (x1 - unit <= xStart && x1 != xStart) {
            Square item = new Square(xStart, x1, y1, y2);
            list.add(item);
        }
        square.setAdjoinList(list);
    }

    /**
     * 判断聚合区域中的区域是否和目标区域相邻
     * 
     * @param list
     * @param square
     * @return
     */
    private static boolean isAdjoin(List<Square> list, Square square) {
        boolean isOwn = false;
        for (Square item : list) {
            if (computeIsAdjoin(item, square))
                isOwn = true;
        }
        return isOwn;
    }

    /**
     * 判断两个区域是否相邻
     * 
     * @param s1
     * @param s2
     * @return
     */
    private static boolean computeIsAdjoin(Square s1, Square s2) {
        double s1x1 = s1.getX1();
        double s1x2 = s1.getX2();
        double s1y1 = s1.getY1();
        double s1y2 = s1.getY2();
        double s2x1 = s2.getX1();
        double s2x2 = s2.getX2();
        double s2y1 = s2.getY1();
        double s2y2 = s2.getY2();
        if (s1x1 == s2x1 && s1x2 == s2x2 && (s1y1 == s2y1 || s1y2 == s2y2)) {
            return true;
        }
        if (s1y1 == s2y1 && s1y2 == s2y2 && (s1x1 == s2x1 || s1x2 == s2x2))
            return true;
        return false;
    }

    /**
     * 生成向量
     * 
     * @param countList
     * @return
     */
    public static List<Point> generateVector(List<Double> countList) {
        List<Point> vectors = new ArrayList<>();
        for (double count : countList) {
            Point point = new Point(0d, count);
            vectors.add(point);
        }
        return vectors;
    }

}
