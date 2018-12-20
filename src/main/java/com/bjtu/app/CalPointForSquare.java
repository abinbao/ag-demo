package com.bjtu.app;

import java.util.ArrayList;
import java.util.List;

import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;
import com.bjtu.util.SquareUtils;

/**
 * 统计每个网格中的数据点
 * 
 * @author apple
 */
public class CalPointForSquare {

    public static void main(String[] args) {
        // 开始加载数据点
        ArrayList<Point> pointList = (ArrayList<Point>) FileUtil.loadata();
        // 开始划分网格
        List<Square> squareList = SquareUtils.divideSquare(360, 360, 5);
        // 开始统计每个网格中的数据点
        CalUtil.calPointNum(squareList, pointList);
        // 打印 划分好的网格中 count 大于 0 的区域
        for (Square square : squareList) {
            if (square.getCount() > 0) {
                System.out.println("x1:" + square.getX1() + ", x2:" + square.getX2() + ", y1:" + square.getY1()
                        + ", y2:" + square.getY2() + ", count:" + square.getCount());
            }
        }
    }

}
