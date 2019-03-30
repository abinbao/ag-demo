package com.bjtu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.pipeline.StationInfoDao;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;
import com.bjtu.util.PropertiesUtil;
import com.bjtu.util.SquareUtils;

/**
 * 统计空值的区域
 * 
 * @author apple
 */
public class SumSquareOfNull {

    private static Logger logger = LoggerFactory.getLogger(SumSquareOfNull.class);
    private static Properties prop = PropertiesUtil.loadProps("/config.properties");

    private static double len = 320.0;
    private static double height = 160.0;
    private static double unit = 1;

    public static void main(String[] args) {
        // 1. 首先读取点的个数
        List<Point> pointList = null;
        if (Boolean.parseBoolean(prop.getProperty("chekin.enabled")))
            pointList = FileUtil.loadata();
        else
            pointList = StationInfoDao.getPoints();
        logger.info(" === >>> 共读取：{} 个点", pointList.size());
        // 2. 划分区域
        Map<String, Square> squareMap = new HashMap<>();
        List<Square> squareList = SquareUtils.divideSquare(len, height, unit, squareMap);
        logger.info(" === >>> 共划分区域：{} 个", squareList.size());
        // 3. 统计每个区域中点的个数
        CalUtil.calPointNum(squareList, pointList);
        logger.info(" === >>> 区域统计结束 <<< === ");
        ArrayList<Double> countList = new ArrayList<>(); // 统计点的集合
        // 打印 划分好的网格中 count 大于 0 的区域
        int count = 0;
        int index = 0;
        int max = 0;
        int min = 0;
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

    }
}
