package com.bjtu.main;

import java.util.ArrayList;
import java.util.List;

import com.bjtu.canopy.Canopy;
import com.bjtu.canopy.CanopyCluster;
import com.bjtu.kmeans.KmeansCluster;
import com.bjtu.kmeans.KmeansRunner;
import com.bjtu.model.Point;

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

    public static void runCluster(List<Point> points) {
        CanopyCluster builder = new CanopyCluster(8d, 4d, points);
        builder.runCluster();
        List<Canopy> canopies = builder.getCanopies();
        List<Point> centers = new ArrayList<>();
        for (Canopy canopy : canopies) {
            centers.add(canopy.getCenter());
            System.out.println(canopy.toString());
        }
        // Kmeans 聚类
        KmeansRunner kmeans = KmeansRunner.createKmeansCluster(centers.size(), 50, points, centers);
        kmeans.initClusters();
        kmeans.runKmeansCluster();
        List<KmeansCluster> clusters = kmeans.getClusters();
        for (KmeansCluster cluster : clusters) {
            System.out.println(cluster.toString());
        }
    }
}
