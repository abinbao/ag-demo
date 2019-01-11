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
        points.add(new Point(0.0, 2.1));
        points.add(new Point(0.0, 1.1));
        points.add(new Point(0.0, 9.0));
        points.add(new Point(0.0, 27.0));
        points.add(new Point(0.0, 33.0));
        runCluster(points);
    }

    // 开始聚类
    public static void runCluster(List<Point> points) {
        // 注意这里，由于List 是引用类型，传入 Canopy 算法时，Canopy 对 List 进行了删除操作，导致测试集合的测试点变少
        // 所以使用另一个 List 来保存测试结合防止印象接下来的运算
        List<Point> kpoints = new ArrayList<>();
        kpoints.addAll(points);
        CanopyCluster builder = new CanopyCluster(8d, 4d, points);
        builder.runCluster();
        List<Canopy> canopies = builder.getCanopies();
        List<Point> centers = new ArrayList<>();
        for (Canopy canopy : canopies) {
            centers.add(canopy.getCenter());
            System.out.println(canopy.toString());
        }
        // Kmeans 聚类
        KmeansRunner kmeans = KmeansRunner.createKmeansCluster(centers.size(), 50, kpoints, centers);
        kmeans.initClusters();
        kmeans.runKmeansCluster();
        List<KmeansCluster> clusters = kmeans.getClusters();
        for (KmeansCluster cluster : clusters) {
            System.out.println(cluster.toString());
        }
    }
}
