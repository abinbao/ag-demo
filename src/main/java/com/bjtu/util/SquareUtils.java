package com.bjtu.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static List<Square> divideSquare(double xLength, double yLength, double unit) {
        List<Square> squareList = new ArrayList();
        double xStart = -(xLength / 2);
        double xEnd = (xLength / 2);
        double yStart = -(yLength / 2);
        double yEnd = (yLength / 2);
        for (double x = xStart; x < xEnd; x = x + unit) {
            double x2 = x + 5;
            if (x + 5 > xEnd) {
                x2 = xEnd;
            }
            for (double y = yStart; y < yEnd; y = y + 5) {
                double y2 = y + 5;
                if (y + 5 > yEnd) {
                    y2 = yEnd;
                }
                Square square = new Square(x, x2, y, y2);
                squareList.add(square);
            }
        }
        return squareList;
    }
}
