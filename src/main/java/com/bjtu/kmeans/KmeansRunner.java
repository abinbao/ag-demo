package com.bjtu.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bjtu.canopy.distance.AbstractDistanceComputor;
import com.bjtu.canopy.distance.EuclideanDistanceComputor;
import com.bjtu.model.Point;

/**
 * Kmeans 聚类
 * 
 * @author apple
 */
public class KmeansRunner {

    public static final double MINDISTANCE = 10000.00;
    // 聚类中心数
    private int k = 5;
    // 迭代最大次数
    private int maxIter = 50;
    // 测试点集
    private List<Point> points;
    // 中心点
    private List<Point> centers;
    // 生成的簇
    private List<KmeansCluster> kmeansClusters;

    private AbstractDistanceComputor computor;

    /**
     * 返回聚类结果
     * 
     * @return
     */
    public List<KmeansCluster> getClusters() {
        return kmeansClusters;
    }

    /**
     * 执行 kmeans 聚类
     * 
     * @return
     */
    public static KmeansRunner createKmeansCluster() {
        return new KmeansRunner();
    }

    public KmeansRunner() {
    }

    public KmeansRunner(int k, int maxIter, List<Point> points, List<Point> centers) {
        this(k, maxIter, points, centers, new EuclideanDistanceComputor());
    }

    /**
     * @param k
     * @param maxIter
     * @param points
     * @return
     */
    public static KmeansRunner createKmeansCluster(int k, int maxIter, List<Point> points, List<Point> centers) {
        return new KmeansRunner(k, maxIter, points, centers);
    }

    public KmeansRunner(int k, int maxIter, List<Point> points, List<Point> centers,
            AbstractDistanceComputor computor) {
        this.k = k;
        this.maxIter = maxIter;
        this.points = points;
        this.centers = centers;
        this.computor = computor;
        this.kmeansClusters = new ArrayList<>();
    }

    /**
     * 初始化簇
     */
    public void initClusters() {
        if (!centers.isEmpty()) {
            for (int i = 0; i < centers.size(); ++i) {
                KmeansCluster kmeans = new KmeansCluster();
                kmeans.setCenter(centers.get(i));
                kmeans.setClusterId(i + 1);
                kmeans.getPoints().add(centers.get(i));
                kmeansClusters.add(kmeans);
            }
        }
    }

    /**
     * 停止条件是满足迭代次数
     */
    public void runKmeansCluster() {
        // 已迭代次数
        int count = 1;
        while (count++ <= maxIter) {
            // 遍历每个点，确定其所属簇
            for (Point point : points) {
                assignPointToCluster(point);
            }
            // 调整中心点
            handleCenters();
        }
    }

    /*
     * 调整聚类中心，按照求平衡点的方法获得新的簇心
     */
    public void handleCenters() {
        for (KmeansCluster cluster : kmeansClusters) {
            Set<Point> pointsCluster = cluster.getPoints();
            Point center = new Point();
            double sumx = 0.0;
            double sumy = 0.0;
            int count = pointsCluster.size();
            for (Point point : pointsCluster) {
                sumx = sumx + point.getX();
                sumy = sumy + point.getY();
            }
            center.setX(sumx / count);
            center.setY(sumy / count);
            cluster.setCenter(center);
        }
    }

    /*
     * 划分点到某个簇中，欧式距离标准 对传入的每个点，找到与其最近的簇中心点，将此点加入到簇
     */
    public void assignPointToCluster(Point point) {
        double minDistance = MINDISTANCE;
        KmeansCluster belong = null;
        for (KmeansCluster cluster : kmeansClusters) {
            double dis = computor.computeDistance(point, cluster.getCenter());
            if (dis < minDistance) {
                minDistance = dis;
                belong = cluster;
            }
        }
        if (null != belong)
            belong.getPoints().add(point);

    }
}
