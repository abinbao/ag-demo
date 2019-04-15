package com.bjtu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.pipeline.StationInfoDao;
import com.bjtu.util.FileUtil;
import com.bjtu.util.PropertiesUtil;

/**
 * 统计空值的区域
 * 
 * @author apple
 */
public class SumSquareOfNull {

    private static Logger logger = LoggerFactory.getLogger(SumSquareOfNull.class);
    private static Properties prop = PropertiesUtil.loadProps("/config.properties");

    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static double unit = 0.04; // 划分区域单元格

    public static void main(String[] args) {
        // 1. 首先读取点的个数
        List<Point> pointList = null;
        // 根据 chekin.enable 参数判断需要读取那个数据集
        // 数据集包含 chekin 数据集， station 数据集， 参数为 true 时，读取 chekin 数据集
        if (Boolean.parseBoolean(prop.getProperty("chekin.enabled")))
            pointList = FileUtil.loadata();
        else
            pointList = StationInfoDao.getPoints();
        logger.info(" === >>> 共读取：{} 个点", pointList.size());
        // 2. 划分区域
        Map<String, Square> squareMap = new HashMap<>();
        List<Square> squareList = divideSquare(unit, squareMap);
        logger.info(" === >>> 共划分区域：{} 个", squareList.size());
        // 3. 统计每个区域中点的个数
        calPointNum(squareList, pointList);
        logger.info(" === >>> 区域统计结束 <<< === ");
        ArrayList<Double> countList = new ArrayList<>(); // 统计点的集合
        // 打印 划分好的网格中 count 大于 0 的区域

        int count = 0; // 统计不包含点的区域个数
        int index = 0; // 统计包含点的区域个数
        int max = 0; // 统计包含点区域的点个数的最大值
        int min = 0; // 统计包含区域的点个数的最小值
        for (Square square : squareList) {
            if (square.getCount() >= max) {
                max = square.getCount();
            }
            if (square.getCount() <= min) {
                min = square.getCount();
            }
            if (square.getCount() > 0) {
                index++;
                logger.info("x1:" + square.getX1() + ", x2:" + square.getX2() + ", y1:" + square.getY1() + ", y2:"
                        + square.getY2() + ", count:" + square.getCount());
            } else {
                count++;
            }
            countList.add((double) square.getCount()); // 向点的集合中添加点
        }
        logger.info("区域包含点的最大值为：" + max);
        logger.info("区域包含点的最大值为：" + min);
        logger.info("不包含点的区域共有：" + count);
        logger.info("包含点的区域共有：" + index);
        pool.shutdown();

    }

    public static void calPointNum(List<Square> squareList, List<Point> pointList) {
        ArrayList<Point> temp = (ArrayList<Point>) pointList;
        long start = System.currentTimeMillis();
        List<Future<Integer>> futures = new ArrayList<>();
        for (Square square : squareList) {
            futures.add(pool.submit(new Task(temp, square)));
        }
        for (Future<Integer> future : futures) {
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        logger.info("共提交: " + futures.size() + " 个任务，运行时间 " + (System.currentTimeMillis() - start));
    }

    private static class Task implements Callable<Integer> {

        private ArrayList<Point> temp;

        private Square square;

        public Task(ArrayList<Point> temp, Square square) {
            this.temp = temp;
            this.square = square;
        }

        @Override
        public Integer call() throws Exception {

            for (Iterator<Point> it = temp.iterator(); it.hasNext();) {
                Point point = it.next();
                if (point.getX() >= square.getX1() && point.getX() <= square.getX2() && point.getY() >= square.getY1()
                        && point.getY() <= square.getY2()) {
                    square.setCount(square.getCount() + 1);
                }
            }
            return square.getCount();
        }

    }

    public static List<Square> divideSquare(double unit, Map<String, Square> squareMap) {
        List<Square> squareList = new ArrayList();
        double xStart = 18;
        double xEnd = 55;
        double yStart = 90;
        double yEnd = 136;
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
                squareList.add(square);
            }
        }
        return squareList;
    }
}
