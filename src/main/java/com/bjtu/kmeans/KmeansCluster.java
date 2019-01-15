package com.bjtu.kmeans;

import java.util.HashSet;
import java.util.Set;

import com.bjtu.model.Point;

/**
 * Kmeans 算法
 * 
 * @author apple
 */
public class KmeansCluster {
    // 簇 id
    private int clusterId;
    // 簇的中心点
    private Point center;
    // 簇包含的 点集
    private Set<Point> points;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Set<Point> getPoints() {
        if (null == points) {
            points = new HashSet<>();
        }
        return points;
    }

    @Override
    public String toString() {
        return "Kmeans [ClusterId=" + clusterId + ", center=" + center + ", points=" + points + "]";
    }

    /**
     * 判断该簇是否包含这个点
     * 
     * @param point
     * @return
     */
    public boolean coverCluster(Point point) {
        return points.contains(point);
    }

}
