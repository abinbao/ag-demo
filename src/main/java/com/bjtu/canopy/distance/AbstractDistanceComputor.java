package com.bjtu.canopy.distance;

import com.bjtu.model.Point;

/**
 * 距离算法接口
 * 
 * @author apple
 */
public interface AbstractDistanceComputor {

    public double computeDistance(Point a, Point b);
}
