package com.bjtu.main;

import java.util.ArrayList;
import java.util.List;

import com.bjtu.canopy.Canopy;
import com.bjtu.canopy.CanopyCluster;
import com.bjtu.kmeans.KmeansCluster;
import com.bjtu.kmeans.KmeansRunner;
import com.bjtu.model.Point;

/**
 * Canopy 算法 结合 Kmeans 算法
 * 
 * @author apple
 */
public class CanopyWithKmeansCluster {

    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 1000.0));
        points.add(new Point(0.0, 20.0));
        points.add(new Point(0.0, 9.0));
        points.add(new Point(0.0, 5000.0));
        points.add(new Point(0.0, 300.0));
        Point p = new Point(0.0, 9.0);
        System.out.println(points.contains(p));
        //runCluster(points);
    }

    // 开始聚类
    public static List<KmeansCluster> runCluster(List<Point> points) {
        // 注意这里，由于List 是引用类型，传入 Canopy 算法时，Canopy 对 List 进行了删除操作，导致测试集合的测试点变少
        // 所以使用另一个 List 来保存测试结合防止印象接下来的运算
        List<Point> kpoints = new ArrayList<>();
        kpoints.addAll(points);
        // 初始化 Canopy 参数
        CanopyCluster builder = new CanopyCluster(5d, 1d, points);
        // 开始 Canopy 算法
        builder.runCluster();
        // 获取 Canopy
        List<Canopy> canopies = builder.getCanopies();
        // 遍历中心点
        List<Point> centers = new ArrayList<>();
        for (Canopy canopy : canopies) {
            centers.add(canopy.getCenter());
            System.out.println(canopy.toString());
        }
        // Kmeans 聚类
        KmeansRunner kmeans = KmeansRunner.createKmeansCluster(centers.size(), 50, kpoints, centers);
        // 初始化 Kmeans 参数
        kmeans.initClusters();
        // 开始聚类
        kmeans.runKmeansCluster();
        // 获取 Kmeans 簇
        List<KmeansCluster> clusters = kmeans.getClusters();
        for (KmeansCluster cluster : clusters) {
            System.out.println(cluster.toString());
        }
        return clusters;
    }
}
