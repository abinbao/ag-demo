package com.bjtu.mahout.kmeans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;

import com.bjtu.mahout.canopy.RandomPointsUtil;

public class KMeansWithCanopyClustering {

    public static void main(String args[]) throws Exception {
        List<Vector> sampleData = new ArrayList<>();

        RandomPointsUtil.generateSamples(sampleData, 400, (double) 1, (double) 1, (double) 3);
        RandomPointsUtil.generateSamples(sampleData, 300, (double) 1, (double) 0, (double) 0.5);
        RandomPointsUtil.generateSamples(sampleData, 300, (double) 0, (double) 2, (double) 0.1);
        String inputDir = "reuters-vectors";
        // 样本文件
        File file = new File(inputDir);
        if (!file.exists())
            file.mkdirs();
        Configuration conf = new Configuration();
        String vectorsFolder = inputDir + "/simple-vectors";
        Path samples = new Path(vectorsFolder + "/part-r-00000");
        // 将样本数据写入到样本文件中
        ClusterHelper.writePointsToFile(sampleData, conf, samples);

        // 输出结果文件
        Path output = new Path("output");
        HadoopUtil.delete(conf, output);

        // Canopy聚类中心点
        Path canopyCentroids = new Path(output, "canopy-centroids");
        Path clusterOutput = new Path(output, "clusters");

        CanopyDriver.run(conf, samples, canopyCentroids, new CosineDistanceMeasure(), 0.7, 0.5, false, 0, false);

        KMeansDriver.run(conf, samples, new Path(canopyCentroids, "clusters-0-final"), clusterOutput, 0.01, 20, true,
                0.0, false);

        List<List<Cluster>> Clusters = ClusterHelper.readClusters(conf, clusterOutput);
        for (Cluster cluster : Clusters.get(Clusters.size() - 1)) {
            Vector item = new DenseVector(new double[] { 1.5, 2.5 });
            System.out
                    .println("=====>>>>>" + new Kluster(sampleData.get(0), cluster.getId(), new CosineDistanceMeasure())
                            .calculateConvergence(0.01));
            System.out.println("Cluster id: " + cluster.getId() + " center: " + cluster.getCenter().asFormatString()
                    + "====>>>>" + cluster.getClass().getSimpleName());
        }
    }
}
