package com.bjtu.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.LaplaceDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.config.Config;
import com.bjtu.model.LineX;
import com.bjtu.model.LineY;
import com.bjtu.model.Point;
import com.bjtu.model.Square;

/**
 * 计算的一些方法
 */
public class CalUtil {

    private static Logger logger = LoggerFactory.getLogger(CalUtil.class);

    /**
     * @param n
     *            读取的数据行数，也就是点数
     * @param e
     * @param c
     * @return 返回 m1 值
     */
    public static int calGridNum(int n, double e, int c) {
        int m1;
        m1 = (int) Math.max(10, Math.ceil(0.25 * Math.sqrt((n * e) / c)));
        return m1;
    }

    /**
     * @param count
     * @param a
     * @param e
     * @param c
     * @return
     */
    public static int calGridNum2(double count, double a, double e, double c) {
        return (int) Math.ceil(Math.sqrt(count * (1 - a) * e) / c);
    }

    /**
     * @param m1
     *            网格的个数的开方
     * @param x_length
     *            地区的长
     * @param y_length
     *            地区的宽
     * @return
     */
    public static List<Square> divideGridFir(int m1, double xLength, double yLength) {

        List<Square> list = new ArrayList<Square>();

        NumberFormat nbf = NumberFormat.getInstance();
        nbf.setMinimumFractionDigits(2);
        double xUnit = xLength / m1;
        double yUnit = yLength / m1;

        double xBottom = -(xLength / 2);
        double yBottom = -(yLength / 2);

        for (int i = 0; i < m1; i++) {
            double temp = yBottom;
            for (int j = 0; j < m1; j++) {
                Square square = new Square(Config.formatDouble(xBottom),
                        Double.parseDouble(nbf.format(xBottom + xUnit)), Config.formatDouble(temp),
                        Double.parseDouble(nbf.format(temp + yUnit)));
                temp = Double.parseDouble(nbf.format(temp + yUnit));
                list.add(square);
            }
            xBottom = Double.parseDouble(nbf.format(xBottom + xUnit));
        }
        return list;
    }

    /**
     * @param square
     *            需要第二次划分的区域
     */
    public static void divideGridSec(Square square) {

        double x1 = square.getX1();
        double x2 = square.getX2();
        double y1 = square.getY1();
        double y2 = square.getY2();

        int m2 = calGridNum2(square.getCount(), 0.5, 0.1, 5);
        square.setM2(m2);
        if (m2 == 1 || m2 == 0)
            return;
        NumberFormat nbf = NumberFormat.getInstance();
        nbf.setMinimumFractionDigits(2);
        double xUnit = (x2 - x1) / m2;
        double yUnit = (y2 - y1) / m2;

        double xBottom = x1;
        double yBottom = y1;

        for (int i = 0; i < m2; i++) {
            double temp = yBottom;
            for (int j = 0; j < m2; j++) {
                Square squareTemp = new Square(Config.formatDouble(xBottom),
                        Double.parseDouble(nbf.format(xBottom + xUnit)), Config.formatDouble(temp),
                        Double.parseDouble(nbf.format(temp + yUnit)));
                temp = Double.parseDouble(nbf.format(temp + yUnit));
                square.getSquareList().add(squareTemp);
            }
            xBottom = Double.parseDouble(nbf.format(xBottom + xUnit));
        }
        square.setAvg(square.getCount() / square.getSquareList().size());
    }

    /**
     * @desc 确定每个区域的点的个数和加入拉普拉斯噪声的个数
     * @param squareList
     *            区域集合
     * @param pointList
     *            点集合
     */
    public static void calPointNum(List<Square> squareList, List<Point> pointList) {
        ArrayList<Point> temp = (ArrayList<Point>) pointList;

        for (Square square : squareList) {
            for (Point point : temp) {
                if (point.getX() >= square.getX1() && point.getX() <= square.getX2() && point.getY() >= square.getY1()
                        && point.getY() <= square.getY2()) {
                    square.setCount(square.getCount() + 1);
                }
            }
            // 加入拉普拉斯噪声
            if (square.getCount() != 0) {
                double countLap = lapalceNoice(square.getCount(), 0.1);
                square.setCountLa(countLap);
            } else {
                square.setCountLa(0);
            }
        }
    }

    /**
     * @param count
     *            需要加入拉普拉丝的噪声
     * @param epsilon
     *            调节参数
     * @return
     */
    public static double lapalceNoice(long count, double epsilon) {
        LaplaceDistribution ld = new LaplaceDistribution(0, 1 / epsilon);
        double noise = ld.sample();
        return count + noise;
    }

    /**
     * 查询区域实际点的个数
     * 
     * @param query
     * @param pointList
     */
    public static int searchActualPointNum(Square query, List<Point> pointList) {

        int count = 0;

        for (Point point : pointList) {

            double x = point.getX();
            double y = point.getY();

            if (x >= query.getX1() && x <= query.getX2() && y >= query.getY1() && y <= query.getY2()) {
                count = count + 1;
            }

        }
        return count;
    }

    /**
     * @param square
     *            统计区域
     * @param Agcount
     *            加入拉普拉斯的噪声
     * @param query
     *            查询区域
     */
    public static void searchLapalcePointNum(Square square, Map<String, Double> Agcount, Square query) {

        double temp = Agcount.get("agNum");
        if (!square.isFlag()) {
            double x3 = square.getX1();
            double x4 = square.getX2();
            double y3 = square.getY1();
            double y4 = square.getY2();

            double x1 = query.getX1();
            double x2 = query.getX2();
            double y1 = query.getY1();
            double y2 = query.getY2();

            double squareArea = (x4 - x3) * (y4 - y3); // 区域面积
            double rate = 0.0;
            // 1.全部在区域中
            if (x3 >= x1 && x4 <= x2 && y3 >= y1 && y4 <= y2) {
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa());
            }
            // 2. 左上角
            else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
                rate = (x4 - x1) * (y2 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 3. 左边不包含
            else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 >= y1 && y4 <= y2) {
                rate = (x4 - x1) * (y4 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 4. 左下角
            else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
                rate = (x4 - x1) * (y4 - y1) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 5. 下边
            else if (x3 >= x1 && x4 <= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
                rate = (x4 - x3) * (y4 - y1) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 6.右下角
            else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
                rate = (x2 - x3) * (y4 - y1) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 7.右边
            else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 >= y1 && y4 <= y2) {
                rate = (x2 - x3) * (y4 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 8. 右上角
            else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
                rate = (x2 - x3) * (y2 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 9.上边
            else if (x3 >= x1 && x4 <= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
                rate = (x4 - x3) * (y2 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 10. 上边包含
            else if (x3 <= x1 && x4 >= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
                rate = (x2 - x1) * (y2 - y3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 11.下边包含
            else if (x3 <= x1 && x4 >= x2 && y3 <= y1 && y4 <= y2 && y4 >= y1) {
                rate = (x2 - x1) * (y4 - y1) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 12. 左边包含
            else if (y3 <= y1 && y4 >= y2 && x3 <= x1 && x4 <= x2 && x4 >= x1) {
                rate = (y2 - y1) * (x2 - x3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }
            // 13. 右边包含
            if (y3 <= y1 && y4 >= y2 && x3 >= x1 && x3 <= x2 && x4 >= x2) {
                rate = (y2 - y1) * (x2 - x3) / squareArea;
                Agcount.put("agNum", Agcount.get("agNum") + square.getCountLa() * rate);
            }

            if (temp != Agcount.get("agNum")) {
                Agcount.put("squareNum", Agcount.get("squareNum") + 1);
            }

            logger.info(square.toString() + "===" + Agcount.get("agNum") + "==rate==" + rate + "== count_lap=="
                    + square.getCountLa() + "==count==" + square.getCount());
        } else {
            // if(!square.getSquareList().isEmpty()) {
            // for(Square item : square.getSquareList()) {
            // searchLapalcePointNum(item,Agcount,query);
            // }
            // }
            searchLapalcePointNum(square.getSquare_D1(), Agcount, Config.querySquare);
            searchLapalcePointNum(square.getSquare_D2(), Agcount, Config.querySquare);
        }
    }

    /**
     * 查询合并区域覆盖面积
     * 
     * @param square
     * @param Agcount
     * @param query
     */
    public static void searchLapalcePointNumByCluster(Square square, Map<String, Double> result, Square query) {

        double x3 = square.getX1();
        double x4 = square.getX2();
        double y3 = square.getY1();
        double y4 = square.getY2();

        double x1 = query.getX1();
        double x2 = query.getX2();
        double y1 = query.getY1();
        double y2 = query.getY2();

        // 1.全部在区域中
        String mergeId = square.getMergeId();
        if (x3 >= x1 && x4 <= x2 && y3 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + square.getSquare()) : square.getSquare());
        }
        // 2. 左上角
        else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x4 - x1) * (y2 - y3)) : square.getSquare());
        }
        // 3. 左边不包含
        else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x4 - x1) * (y4 - y3)) : square.getSquare());
        }
        // 4. 左下角
        else if (x3 <= x1 && x4 >= x1 && x4 <= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x4 - x1) * (y4 - y1)) : square.getSquare());
        }
        // 5. 下边
        else if (x3 >= x1 && x4 <= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x4 - x3) * (y4 - y1)) : square.getSquare());
        }
        // 6.右下角
        else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 <= y1 && y4 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x2 - x3) * (y4 - y1)) : square.getSquare());
        }
        // 7.右边
        else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 >= y1 && y4 <= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x2 - x3) * (y4 - y3)) : square.getSquare());
        }
        // 8. 右上角
        else if (x3 >= x1 && x3 <= x2 && x4 >= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x2 - x3) * (y2 - y3)) : square.getSquare());
        }
        // 9.上边
        else if (x3 >= x1 && x4 <= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x4 - x3) * (y2 - y3)) : square.getSquare());
        }
        // 10. 上边包含
        else if (x3 <= x1 && x4 >= x2 && y3 >= y1 && y3 <= y2 && y4 >= y2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x2 - x1) * (y2 - y3)) : square.getSquare());
        }
        // 11.下边包含
        else if (x3 <= x1 && x4 >= x2 && y3 <= y1 && y4 <= y2 && y4 >= y1) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (x2 - x1) * (y4 - y1)) : square.getSquare());
        }
        // 12. 左边包含
        else if (y3 <= y1 && y4 >= y2 && x3 <= x1 && x4 <= x2 && x4 >= x1) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (y2 - y1) * (x2 - x3)) : square.getSquare());
        }
        // 13. 右边包含
        if (y3 <= y1 && y4 >= y2 && x3 >= x1 && x3 <= x2 && x4 >= x2) {
            result.put(mergeId,
                    result.containsKey(mergeId) ? (result.get(mergeId) + (y2 - y1) * (x2 - x3)) : square.getSquare());
        }

    }

    /**
     * 判断区域是否需要分割
     * 
     * @param e
     * @param square
     */
    @Deprecated
    public static void judgeDivideOld(double e, Square square) {
        double temp = 0.0;
        if (!square.getSquareList().isEmpty()) {
            for (Square item : square.getSquareList()) {
                temp = temp + Math.abs((double) item.getCount() - square.getAvg());
            }
            if (temp >= e * square.getCount()) {
                logger.info(square.toString() + temp + "======= 需要继续分割 =====");
                square.setFlag(true);
                return;
            }
        }
    }

    /**
     * @param e
     * @param square
     */
    public static void judgeDivide(double e, Square square) {
        double temp = 0.0;
        if (!square.getSquareList().isEmpty() && square.getCount() != 0) {
            for (Square item : square.getSquareList()) {

                double b = item.getCount(); // count(ci)
                double c = square.getCount(); // Ni

                double d = b / c;
                double f = getLogValue(d);

                temp = temp + d * f;
            }

            // 阈值
            double a = 1 / (double) square.getSquareList().size();
            double threshold = -e * (getLogValue(a));

            square.setTemp(-temp);
            square.setThrelod(threshold);
            logger.info("当前值：" + (-temp) + ", 阈值为：" + threshold);

            if (-temp < threshold) {
                logger.info(square.toString() + temp + "======= 需要继续分割 =====");
                square.setFlag(true);
            }
        }
    }

    /**
     * @param a
     * @return
     */
    public static double getLogValue(double a) {
        double result = 0.0;
        if (a != 0)
            result = Math.log(a) / Math.log((double) 2);
        return result;
    }

    /**
     * 生成 xy 分割线
     * 
     * @param square
     */
    public static void genLineXY(Square square) {

        double x1 = square.getX1();
        double x2 = square.getX2();
        double y1 = square.getY1();
        double y2 = square.getY2();

        int m2 = calGridNum2(square.getCount(), 0.5, 0.1, 5);
        square.setM2(m2);
        if (m2 == 0 || m2 == 1)
            return;
        double xUnit = (x2 - x1) / m2;
        double xBottom = x1;
        for (int i = 0; i < m2 - 1; i++) {
            LineX lineX = new LineX(Config.formatDouble(xBottom + xUnit), y1, y2);
            square.getLineXList().add(lineX);
            xBottom = xBottom + xUnit;
        }

        double yUnit = (y2 - y1) / m2;
        double yBottom = y1;
        for (int i = 0; i < m2 - 1; i++) {
            LineY lineY = new LineY(x1, x2, Config.formatDouble(yBottom + yUnit));
            square.getLineYList().add(lineY);
            yBottom = yBottom + yUnit;
        }
        // logger.info(square.toString() + "x轴分割线为:" +
        // square.getLineXList().size() + " 条, y轴分割线为：" +
        // square.getLineYList().size() + " 条.");
    }

    /**
     * @desc 如果区域需要继续划分找到最佳分割线
     * @param square
     * @param pointList
     */
    public static void findGoldLine(Square square, List<Point> pointList) {

        logger.info(square.toString() + "x轴分割线为:" + square.getLineXList().size() + " 条, y轴分割线为："
                + square.getLineYList().size() + " 条.");
        if (square.getLineXList().isEmpty() && square.getLineYList().isEmpty()) {
            return;
        }
        if (square.getCount() == 0)
            return;
        //
        if (!square.getLineXList().isEmpty() && square.getLineYList().isEmpty()) {
            LineX goldLineX = square.getLineXList().get(0);
            int xValue = calLineXValue(goldLineX, square, pointList);
            for (int i = 1; i < square.getLineXList().size(); i++) {
                int temp = calLineXValue(square.getLineXList().get(i), square, pointList);
                if (temp < xValue) {
                    goldLineX = square.getLineXList().get(i);
                    xValue = temp;
                }
            }
            square.setGold_x_line(goldLineX);
            Square squareD1one = new Square(Config.formatDouble(square.getX1()), Config.formatDouble(goldLineX.getX1()),
                    Config.formatDouble(square.getY1()), Config.formatDouble(square.getY2()));
            Square squareD2two = new Square(goldLineX.getX1(), square.getX2(), Config.formatDouble(square.getY1()),
                    Config.formatDouble(square.getY2()));
            square.setSquare_D1(squareD1one);
            square.setSquare_D2(squareD2two);
            setLineToSubSuqare(square);
        }
        if (square.getLineXList().isEmpty() && !square.getLineYList().isEmpty()) {
            LineY goldLineY = square.getLineYList().get(0); // y 最佳分割线
            int yValue = calLineYValue(goldLineY, square, pointList);
            for (int i = 1; i < square.getLineYList().size(); i++) {
                int temp = calLineYValue(square.getLineYList().get(i), square, pointList);
                if (temp < yValue) {
                    goldLineY = square.getLineYList().get(i);
                    yValue = temp;
                }
            }
            square.setGold_y_line(goldLineY);
            Square squareD1one = new Square(Config.formatDouble(square.getX1()), Config.formatDouble(square.getX2()),
                    Config.formatDouble(square.getY1()), Config.formatDouble(goldLineY.getY1()));
            Square squareD2two = new Square(Config.formatDouble(square.getX1()), Config.formatDouble(square.getX2()),
                    Config.formatDouble(goldLineY.getY1()), Config.formatDouble(square.getY2()));
            square.setSquare_D1(squareD1one);
            square.setSquare_D2(squareD2two);
            setLineToSubSuqare(square);
        }
        if (!square.getLineXList().isEmpty() && !square.getLineYList().isEmpty()) {
            LineX goldLineX = square.getLineXList().get(0); // x 最佳分割线
            LineY goldLineY = square.getLineYList().get(0); // y 最佳分割线

            int xValue = calLineXValue(goldLineX, square, pointList);
            int yValue = calLineYValue(goldLineY, square, pointList);

            for (int i = 1; i < square.getLineXList().size(); i++) {
                int temp = calLineXValue(square.getLineXList().get(i), square, pointList);
                if (temp < xValue) {
                    goldLineX = square.getLineXList().get(i);
                    xValue = temp;
                }
            }
            for (int i = 1; i < square.getLineYList().size(); i++) {
                int temp = calLineYValue(square.getLineYList().get(i), square, pointList);
                if (temp < yValue) {
                    goldLineY = square.getLineYList().get(i);
                    yValue = temp;
                }
            }
            // 比较 最优的 x轴 分割线 和 y轴 分割线，找出本查询区域的最优分割线
            if (xValue <= yValue) {
                square.setGold_x_line(goldLineX);
                Square squareD1one = new Square(Config.formatDouble(square.getX1()),
                        Config.formatDouble(goldLineX.getX1()), Config.formatDouble(square.getY1()),
                        Config.formatDouble(square.getY2()));
                Square squareD2two = new Square(goldLineX.getX1(), square.getX2(), square.getY1(), square.getY2());
                square.setSquare_D1(squareD1one);
                square.setSquare_D2(squareD2two);
            } else {
                square.setGold_y_line(goldLineY);
                Square squareD1one = new Square(Config.formatDouble(square.getX1()),
                        Config.formatDouble(square.getX2()), Config.formatDouble(square.getY1()),
                        Config.formatDouble(goldLineY.getY1()));
                Square squareD2two = new Square(Config.formatDouble(square.getX1()),
                        Config.formatDouble(square.getX2()), Config.formatDouble(goldLineY.getY1()),
                        Config.formatDouble(square.getY2()));
                square.setSquare_D1(squareD1one);
                square.setSquare_D2(squareD2two);
            }
            // 找到黄金分割线，并且将分割线添加到 squareD(x) 中
            setLineToSubSuqare(square);
            logger.info(square.toString() + "划分合并的区域：" + square.getSquare_D1().toString()
                    + square.getSquare_D2().toString());
        }
    }

    /**
     * @param square
     * @param subSquare
     */
    public static void setLineToSubSuqare(Square square) {

        // 将子区域划分到 squareD(x) 中，同时计算 count 和 avg
        for (Square item : square.getSquareList()) {
            if (item.getX1() >= square.getSquare_D1().getX1() && item.getX2() <= square.getSquare_D1().getX2()
                    && item.getY1() >= square.getSquare_D1().getY1() && item.getY2() <= square.getSquare_D1().getY2()) {
                square.getSquare_D1().getSquareList().add(item);
                square.getSquare_D1().setCount(square.getSquare_D1().getCount() + item.getCount());
                square.getSquare_D1().setCountLa(square.getSquare_D1().getCountLa() + item.getCountLa());
            } else {
                // if(item.getX1() >= square.getSquare_D2().getX1() &&
                // item.getX2() <=
                // square.getSquare_D2().getX2() && item.getY1() >=
                // square.getSquare_D2().getY1() && item.getY2() <=
                // square.getSquare_D2().getY2()) {
                square.getSquare_D2().getSquareList().add(item);
                square.getSquare_D2().setCount(square.getSquare_D2().getCount() + item.getCount());
                square.getSquare_D2().setCountLa(square.getSquare_D2().getCountLa() + item.getCountLa());
            }
        }

        // 将上次分割的线添加到 square_D(x) 中
        for (LineX lineX : square.getLineXList()) {
            if (lineX.getX1() > square.getSquare_D1().getX1() && lineX.getX1() < square.getSquare_D1().getX2()) {
                square.getSquare_D1().getLineXList().add(lineX);
            }
            if (lineX.getX1() > square.getSquare_D2().getX1() && lineX.getX1() < square.getSquare_D2().getX2()) {
                square.getSquare_D2().getLineXList().add(lineX);
            }

        }
        for (LineY lineY : square.getLineYList()) {
            if (lineY.getY1() > square.getSquare_D1().getY1() && lineY.getY1() < square.getSquare_D1().getY2()) {
                square.getSquare_D1().getLineYList().add(lineY);
            }
            if (lineY.getY1() > square.getSquare_D2().getY1() && lineY.getY1() < square.getSquare_D2().getY2()) {
                square.getSquare_D2().getLineYList().add(lineY);
            }
        }
        // 计算 squareD(x) 的 avg 和 count
        double avgD1;
        if (!square.getSquare_D1().getSquareList().isEmpty()) {
            avgD1 = (double) (square.getSquare_D1().getCount()) / (square.getSquare_D1().getSquareList().size());
        } else {
            avgD1 = 0;
        }
        double avgD2;
        if (!square.getSquare_D2().getSquareList().isEmpty()) {
            avgD2 = (double) (square.getSquare_D2().getCount()) / (square.getSquare_D2().getSquareList().size());
        } else {
            avgD2 = 0;
        }
        square.getSquare_D1().setAvg(avgD1);
        square.getSquare_D2().setAvg(avgD2);

    }

    /**
     * @desc 计算 x 分割线的打分
     * @param lineX
     * @param square
     * @param pointList
     */
    public static int calLineXValue(LineX lineX, Square square, List<Point> pointList) {

        int pointD1 = 0; // D1 区域点的个数 v1
        int pointD2 = 0; // D2 区域点的个数 v2

        int squareD1 = 0; // D1 区域 划分区域的个数 n1
        int squareD2 = 0; // D2 区域 划分区域的个数 n2

        for (Point point : pointList) {
            if (point.getX() >= square.getX1() && point.getX() <= lineX.getX1() && point.getY() >= square.getY1()
                    && point.getY() <= square.getY2()) {
                pointD1 = pointD1 + 1;
            }
        }
        pointD2 = square.getCount() - pointD1;
        logger.info(square.toString() + "===" + lineX.toString() + "==pointD2==" + pointD2);
        if (!square.getSquareList().isEmpty()) {
            for (Square item : square.getSquareList()) {
                if (item.getX2() <= lineX.getX1() && item.getX1() >= square.getX1() && item.getY1() >= square.getY1()
                        && item.getY2() <= square.getY2()) {
                    squareD1 = squareD1 + 1;
                } else {
                    squareD2 = squareD2 + 1;
                }
            }
        }
        logger.info(square.toString() + "===" + lineX.toString() + "==squareD2==" + squareD2);
        squareD2 = square.getSquareList().size() - squareD1;

        // 打分
        int value = pointD1 * squareD1 + pointD2 * squareD2;
        return value;
    }

    /**
     * @desc 计算 y 分割线的打分
     * @param lineY
     * @param square
     * @param pointList
     */
    public static int calLineYValue(LineY lineY, Square square, List<Point> pointList) {
        int pointD1 = 0; // D1 区域点的个数 v1
        int pointD2 = 0; // D2 区域点的个数 v2

        int squareD1 = 0; // D1 区域 划分区域的个数 n1
        int squareD2 = 0; // D2 区域 划分区域的个数 n2

        for (Point point : pointList) {
            if (point.getX() >= square.getX1() && point.getX() <= square.getX2() && point.getY() >= lineY.getY1()
                    && point.getY() <= square.getY2()) {
                pointD1 = pointD1 + 1;
            }
        }
        pointD2 = square.getCount() - pointD1;
        logger.info(square.toString() + "===" + lineY.toString() + "==pointD2==" + pointD2);
        if (!square.getSquareList().isEmpty()) {
            for (Square item : square.getSquareList()) {
                if (item.getX2() <= square.getX2() && item.getX1() <= square.getX1() && item.getY1() >= lineY.getY1()
                        && item.getY2() <= square.getY2()) {
                    squareD1 = squareD1 + 1;
                } else {
                    squareD2 = squareD2 + 1;
                }
            }
        }
        logger.info(square.toString() + "===" + lineY.toString() + "==squareD2==" + squareD2);
        squareD2 = square.getSquareList().size() - squareD1;

        // 打分
        int value = pointD1 * squareD1 + pointD2 * squareD2;
        return value;
    }

    /**
     * @param square
     * @param pointList
     */
    public static void recursionMerge(Square square, List<Point> pointList) {
        // 判断 square 是否需要进行分割
        judgeDivide(Config.e, square);

        if (square.isFlag()) {
            findGoldLine(square, pointList);
            if (square.getSquare_D1() == null || square.getSquare_D2() == null) {
                logger.info(square.toString() + " ==== 分割结束 ===");
                square.setFlag(false);
                return;
            }
            recursionMerge(square.getSquare_D1(), pointList);
            recursionMerge(square.getSquare_D2(), pointList);

        } else {
            logger.info(square.toString() + " ==== 分割结束 ===");
            return;
        }
    }

    public static void main(String args[]) {
        int r = calGridNum(10000000, 0.1, 10);
        // System.out.println(r);

        double d = 9.0 / 2;
        // System.out.println(d);

    }
}
