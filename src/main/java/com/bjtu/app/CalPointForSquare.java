package com.bjtu.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.clustering.canopy.Canopy;
import org.apache.mahout.clustering.canopy.CanopyClusterer;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;

import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;
import com.bjtu.util.PropertiesUtil;
import com.bjtu.util.SquareUtils;

/**
 * 统计每个网格中的数据点
 * 
 * @author apple
 */
public class CalPointForSquare {

    private static Properties prop = PropertiesUtil.loadProps("/config.properties");

    public static void main(String[] args) {
        // 开始加载数据点
        ArrayList<Point> pointList = (ArrayList<Point>) FileUtil.loadata();
        System.out.println("===== 点的个数 ======> " + pointList.size());
        // 开始划分网格 divideSquare 划分网格的方法，第一个参数是长，第二个参数是宽，第三个参数是划分单元格
        List<Square> squareList = SquareUtils.divideSquare(360, 180, 5, null);
        System.out.println("===== 区域长度 ======> " + squareList.size());
        // 开始统计每个网格中的数据点
        CalUtil.calPointNum(squareList, pointList);

        ArrayList<Integer> countList = new ArrayList<>(); // 统计点的集合
        // 打印 划分好的网格中 count 大于 0 的区域
        for (Square square : squareList) {
            if (square.getCount() > 0) {
                System.out.println("x1:" + square.getX1() + ", x2:" + square.getX2() + ", y1:" + square.getY1()
                        + ", y2:" + square.getY2() + ", count:" + square.getCount());
            }
            countList.add(square.getCount()); // 向点的集合中添加点
        }
        // 生成点的向量
        List<Vector> vectors = generateVector(countList);
        // 对点的向量进行聚类
        List<Canopy> canopies = CanopyClusterer.createCanopies(vectors, new EuclideanDistanceMeasure(), 3.0, 1.5);

        // 打印聚类集合
        for (Canopy canopy : canopies) {
            System.out.println("Canopy 算法 ===>  聚类簇 id Canopy id: " + canopy.getId() + " 聚类中心点 center: "
                    + canopy.getCenter().asFormatString());
        }
        // =====================================
        // 测试一下
        List<Vector> vectors1 = generateVector(countList);
        // 判断是够属于这个簇
        boolean flag = new CanopyClusterer(new EuclideanDistanceMeasure(), 3.0, 1.5).canopyCovers(canopies.get(0),
                vectors1.get(0));
        if (flag) {
            System.out.println("向量点：" + vectors1.get(0) + " 属于 ===> " + "簇 ：" + canopies.get(0).getId());
        }
        // =====================================
    }

    public static List<Vector> generateVector(List<Integer> countList) {
        List<Vector> vectors = new ArrayList<>();
        for (int count : countList) {
            if (count == 0)
                continue;
            DenseVector vector = new DenseVector(new double[] { 0d, (double) count });
            vectors.add(vector);
        }
        return vectors;

    }

}
