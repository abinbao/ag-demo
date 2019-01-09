package com.bjtu.canopy.distance;

import com.bjtu.model.Point;

/**
 * 曼哈顿距离计算
 * 
 * @author apple
 */
public class MahadunDistanceComputor implements AbstractDistanceComputor {

    @Override
    public double computeDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

}
