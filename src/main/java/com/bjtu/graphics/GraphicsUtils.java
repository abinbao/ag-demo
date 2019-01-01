package com.bjtu.graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JFrame;

public class GraphicsUtils extends JFrame {

    private static final int sx = 50;// 小方格宽度
    private static final int sy = 50;// 小方格高度
    private static final int w = 40;
    private static final int rw = 400;
    private static GraphicsUtils instance = null;
    private transient Graphics jg;
    private Color rectColor = new Color(0xf5f5f5);

    public static GraphicsUtils me() {
        if (instance == null)
            instance = new GraphicsUtils();
        else
            return instance;
        return instance;
    }

    private GraphicsUtils() {
        Container p = getContentPane();
        setBounds(500, 100, 1000, 1000);
        setVisible(true);
        p.setBackground(rectColor);
        setLayout(null);
        setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获取专门用于在窗口界面上绘图的对象
        jg = this.getGraphics();
    }

    public void paintComponents(Color color, int xLength, int yLength, int x1, int x2, int y1, int y2) {
        try {
            // 设置线条颜色为红色
            jg.setColor(color);
            // 绘制外层矩形框
            jg.drawRect(x1 * 10 + xLength, y2 * 10 + yLength, (x2 - x1) * 10, (y2 - y1) * 10);
            jg.fillRect(x1 * 10 + xLength, y2 * 10 + yLength, (x2 - x1) * 10, (y2 - y1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
