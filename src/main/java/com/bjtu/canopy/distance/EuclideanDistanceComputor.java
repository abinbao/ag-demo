package com.bjtu.canopy.distance;

import com.bjtu.model.Point;

/**
 * 欧式距离计算
 * 
 * @author apple
 */
public class EuclideanDistanceComputor implements AbstractDistanceComputor {

    @Override
    public double computeDistance(Point a, Point b) {
        double sum = Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
        return Math.sqrt(sum);
    }

}
