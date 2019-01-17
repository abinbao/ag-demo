package com.bjtu.config;

import java.text.NumberFormat;
import java.util.Random;
import java.util.Set;

import com.bjtu.model.Square;

/**
 * 算法配置
 */
public class Config {

    public static String DATA_PATH = "/Users/apple/project/ag/simple.txt";

    // public static String DATA_PATH = "D:/python/code/xiaobin/simple.txt";

    public static int readNum = 1000; // 读取数据

    public static int startNum = 0; // 开始读的行数

    public static int[] map = { 360, 150 };

    public static Square querySquare = new Square(-320.0, 320.0, -160.0, 160.0);

    public static double e = 1;

    /**
     * 格式化小数 保留两位
     * 
     * @param d
     * @return
     */
    public static Double formatDouble(Double d) {

        NumberFormat nbf = NumberFormat.getInstance();

        nbf.setMinimumFractionDigits(2);

        return Double.parseDouble(nbf.format(d));
    }

    public static void genRandomSquare(double[] param, Set<String> set) {

        double tempX = param[0]; // x 轴的间距
        double tempY = param[1]; // y 轴的间距

        // double temp =

        // int x1 = Math.random()

    }

    /**
     * b > a
     * 
     * @param a
     * @param b
     * @return
     */
    public static int getRandom(int a, int b) {
        if (a > b)
            return 0;
        Random r = new Random();
        return r.nextInt(b - a) - b;
    }

    public static void main(String args[]) {

        System.out.println(getRandom(-10, 10));

    }

}
