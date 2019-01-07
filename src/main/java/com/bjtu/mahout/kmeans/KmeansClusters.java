package com.bjtu.mahout.kmeans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;

import com.bjtu.mahout.canopy.RandomPointsUtil;

/**
 * Kmeans 聚类
 * 
 * @author apple
 */
public class KmeansClusters {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        List<Vector> sampleData = new ArrayList<>();

        RandomPointsUtil.generateSamples(sampleData, 400, (double) 1, (double) 1, (double) 3);
        RandomPointsUtil.generateSamples(sampleData, 300, (double) 1, (double) 0, (double) 0.5);
        RandomPointsUtil.generateSamples(sampleData, 300, (double) 0, (double) 2, (double) 0.1);

        int k = 3;
        File testData = new File("input");
        if (!testData.exists()) {
            testData.mkdir();
        }

        Configuration conf = new Configuration();
        Path samples = new Path("input/file1");
        ClusterHelper.writePointsToFile(sampleData, conf, samples);

        Path output = new Path("output");
        HadoopUtil.delete(conf, output);

        Path clustersIn = new Path(output, "random-seeds");
        DistanceMeasure measure = new EuclideanDistanceMeasure();

        RandomSeedGenerator.buildRandom(conf, samples, clustersIn, k, measure);
        KMeansDriver.run(conf, samples, clustersIn, output, 0.01, 10, true, 0.0, true);

        List<List<Cluster>> Clusters = ClusterHelper.readClusters(conf, output);

        for (List<Cluster> clusterList : Clusters) {
            for (Cluster cluster : clusterList) {
                System.out
                        .println("Cluster id: " + cluster.getId() + " center: " + cluster.getCenter().asFormatString());
            }
            System.out.println();
        }

    }
}
