package com.bjtu.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.config.Config;
import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;

/**
 * @author apple
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {

        logger.info("=== 开始计算 ===");
        logger.info("[ 查询区域范围：x1=" + Config.querySquare.getX1() + ", x2=" + Config.querySquare.getX2() + ", y1="
                + Config.querySquare.getY1() + ", y2=" + Config.querySquare.getY2() + " ]");
        // 加载数据
        ArrayList<Point> pointList = (ArrayList<Point>) FileUtil.loadata();

        // 第一次划分网格数
        int m1 = CalUtil.calGridNum(Config.readNum, 0.1, 10);
        logger.info("第一次划分网格数为：" + m1 * m1);
        // 第一次获取划分的网格
        List<Square> squareList = CalUtil.divideGridFir(m1, Config.map[0], Config.map[1]);
        // 统计第一次划分每个区域的点的个数
        CalUtil.calPointNum(squareList, pointList);
        // 第二次划分网络
        for (Square square : squareList) {
            CalUtil.divideGridSec(square);
        }
        // 统计第二次划分区域中的点的个数
        for (Square square : squareList) {
            CalUtil.calPointNum(square.getSquareList(), pointList);
        }
        logger.info("===== 开始计算 AG 算法 ===== ");
        Map<String, Double> agLapNum = new HashMap<String, Double>();
        agLapNum.put("agNum", (double) 0);
        agLapNum.put("squareNum", (double) 0);

        for (Square square : squareList) {
            if (square.getSquareList().isEmpty()) {
                CalUtil.searchLapalcePointNum(square, agLapNum, Config.querySquare);
            } else {
                for (Square item : square.getSquareList()) {
                    CalUtil.searchLapalcePointNum(item, agLapNum, Config.querySquare);
                }
            }
        }
        int agActNum = CalUtil.searchActualPointNum(Config.querySquare, pointList);

        logger.info("==== AG 算法 结束====");

        logger.info("=== 开始计算 AG_CUBE 算法 ===");
        for (Square square : squareList) {
            // 生成 xy 分割线
            logger.info(square.toString());
            CalUtil.genLineXY(square);
            CalUtil.judgeDivide(Config.e, square);

            if (square.isFlag()) {
                // 找到黄金分割线
                CalUtil.findGoldLine(square, pointList);
                // 判断分割的区域是否还需要再次进行分割
                if (square.getSquare_D1() == null || square.getSquare_D2() == null) {
                    logger.info(square.toString() + " ====没有分割线 分割结束 ===");
                    square.setFlag(false);
                } else {
                    CalUtil.recursionMerge(square.getSquare_D1(), pointList);
                    CalUtil.recursionMerge(square.getSquare_D2(), pointList);
                }
            }
        }
        logger.info("=== AG CUBE 算法 分割完成 ===");

        Map<String, Double> agCubeLapNum = new HashMap<String, Double>();
        agCubeLapNum.put("agNum", (double) 0);
        agCubeLapNum.put("squareNum", 0.0);
        for (Square square : squareList) {
            if (square.getCount() != 0) {
                if (square.isFlag()) {
                    CalUtil.searchLapalcePointNum(square.getSquare_D1(), agCubeLapNum, Config.querySquare);
                    CalUtil.searchLapalcePointNum(square.getSquare_D2(), agCubeLapNum, Config.querySquare);
                } else {
                    CalUtil.searchLapalcePointNum(square, agCubeLapNum, Config.querySquare);

                    // if(!square.getSquareList().isEmpty()) {
                    // for(Square item : square.getSquareList()) {
                    // CalUtil.searchLapalcePointNum(item, agCubeLapNum,
                    // Config.querySquare);
                    // }
                    // } else {
                    // CalUtil.searchLapalcePointNum(square, agCubeLapNum,
                    // Config.querySquare);
                    // }
                }
            } else {
                CalUtil.searchLapalcePointNum(square, agCubeLapNum, Config.querySquare);
            }
        }
        logger.info("AG === 实际查询点的个数为 === " + agActNum);
        logger.info("AG === 加入拉普拉斯噪声查询点的个数为 === " + agLapNum.get("agNum") + ", 共查询区域：" + agLapNum.get("squareNum"));
        logger.info("AG CUBE === 加入拉普拉斯噪声查询点的个数为 === " + agCubeLapNum.get("agNum") + ", 共查询区域："
                + agLapNum.get("squareNum"));
    }
}
