package com.bjtu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.mahout.clustering.canopy.Canopy;
import org.apache.mahout.clustering.canopy.CanopyClusterer;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.model.MergeSquare;
import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;
import com.bjtu.util.SquareUtils;

/**
 * 运行类
 * 
 * @date 2018-12-31
 * @author apple
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static CountDownLatch cdl = null;

    public static void main(String[] args) {

        // 1. 首先读取点的个数
        ArrayList<Point> pointList = (ArrayList<Point>) FileUtil.loadata();
        logger.info("共读取：{} 个点", pointList.size());
        // 2. 划分区域
        Map<String, Square> squareMap = new HashMap<>();
        List<Square> squareList = SquareUtils.divideSquare(16, 8, 5, squareMap);
        logger.info("共划分区域：{} 个", squareList.size());
        // 3. 统计每个区域中点的个数
        CalUtil.calPointNum(squareList, pointList);
        ArrayList<Integer> countList = new ArrayList<>(); // 统计点的集合
        // 打印 划分好的网格中 count 大于 0 的区域
        for (Square square : squareList) {
            if (square.getCount() > 0) {
                logger.info("x1:" + square.getX1() + ", x2:" + square.getX2() + ", y1:" + square.getY1() + ", y2:"
                        + square.getY2() + ", count:" + square.getCount());
            }
            countList.add(square.getCount()); // 向点的集合中添加点
        }
        // 4. 使用 Cannopy 算法构建簇
        // 4.1 生成点的向量
        List<Vector> vectors = generateVector(countList);
        // 4.2 对点的向量进行聚类
        List<Canopy> canopies = CanopyClusterer.createCanopies(vectors, new EuclideanDistanceMeasure(), 3.0, 1.5);
        // 5. 对区域进行打标签，判断集合属于哪个簇
        List<Future> futures = new ArrayList<>();
        cdl = new CountDownLatch(squareList.size());
        for (Square squ : squareList) {
            futures.add(EXECUTOR_SERVICE.submit(new Task(squ, canopies)));
        }
        // 插入数据完成后 执行修改操作
        try {
            cdl.await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(" === >>> 区域 打标签完成...");
        EXECUTOR_SERVICE.shutdown();
        // 6. 对区域进行遍历
        Set<Square> set = new HashSet<>(); // 判断该区域是否被合并
        Map<String, MergeSquare> mergeMap = new ConcurrentHashMap<>(canopies.size());
        for (Square square : squareList) {
            logger.info("当前区域:{}", square.toString());
            if (mergeMap.isEmpty()) {
                logger.info("还没有合并区域,开始构建 ===>>> CanopyId:{}", square.getCanopyId());
                set.add(square);
                buildMergeSquare(square, mergeMap);
                handleAdjoinSuqare(square, set, mergeMap, squareMap);
            } else {
                logger.info("=== >>> 已有合并区域，开始判断当前区域 <<< ===");
                if (set.contains(square)) {
                    logger.info("=== >>> 当前区域已被合并，开始判断其相邻区域 <<< ===");
                    handleAdjoinSuqare(square, set, mergeMap, squareMap);
                } else {
                    logger.info("=== >>> 当前区域还未合并，开始合并 <<< ===");
                    set.add(square);
                    buildMergeSquare(square, mergeMap);
                    handleAdjoinSuqare(square, set, mergeMap, squareMap);
                }
            }
        }
        logger.info(" ===>>> 合并区域大小：{} <<< ===", mergeMap.size());
        for (Square square : squareList) {
            logger.info(square.toString() + " CanopyId:" + square.getCanopyId() + " mergeId:" + square.getMergeId());
        }
    }

    /**
     * 遍历square 相邻区域
     * 
     * @param square
     * @param set
     * @param mergeMap
     */
    public static void handleAdjoinSuqare(Square square, Set<Square> set, Map<String, MergeSquare> mergeMap,
            Map<String, Square> squareMap) {
        for (Square adjoin : square.getAdjoinList()) {
            String key = adjoin.getX1() + "_" + adjoin.getX2() + "_" + adjoin.getY1() + "_" + adjoin.getY2();
            if (set.contains(adjoin) && square.getCanopyId().equals(squareMap.get(key).getCanopyId())) {
                square.setMergeId(squareMap.get(key).getMergeId());
            }

        }
        for (Square adjoin : square.getAdjoinList()) {
            String key = adjoin.getX1() + "_" + adjoin.getX2() + "_" + adjoin.getY1() + "_" + adjoin.getY2();
            String skey = square.getX1() + "_" + square.getX2() + "_" + square.getY1() + "_" + square.getY2();
            if (!set.contains(adjoin) && square.getCanopyId().equals(squareMap.get(key).getCanopyId())) {
                logger.info("adjoin key : {}", key);
                logger.info("square key : {}", skey);
                logger.info("当前 square CanopyId:{}", square.getCanopyId());
                logger.info("当前 adjoin CanopyId:{}", squareMap.get(key).getCanopyId());
                squareMap.get(key).setMergeId(square.getMergeId());
                set.add(adjoin);
                mergeMap.get(square.getMergeId()).getMergeSquare().add(adjoin);
            }
        }
    }

    /**
     * 构建合并区域
     * 
     * @param square
     * @param mergeMap
     */
    private static void buildMergeSquare(Square square, Map<String, MergeSquare> mergeMap) {
        String canopyId = square.getCanopyId();
        MergeSquare mergeItem = new MergeSquare();
        mergeItem.setCanopyId(canopyId); // 聚合区域属于的 canopyId
        String uuid = getUUID();// 生成 mergeId
        mergeItem.setId(uuid);
        List<Square> list = new ArrayList<>();
        list.add(square);
        mergeItem.setMergeSquare(list);
        square.setMergeId(uuid);
        mergeMap.put(uuid, mergeItem);
    }

    private static class Task implements Callable<Boolean> {
        private Square square;
        private List<Canopy> canopies;

        public Task(Square squ, List<Canopy> canopies) {
            this.square = squ;
            this.canopies = canopies;
        }

        @Override
        public Boolean call() throws Exception {
            for (Canopy canopy : canopies) {
                boolean flag = new CanopyClusterer(new EuclideanDistanceMeasure(), 3.0, 1.5).canopyCovers(canopy,
                        get((double) square.getCount()));
                if (flag) {
                    square.setCanopyId(String.valueOf(canopy.getId()));
                }
                cdl.countDown();
            }
            return true;
        }

    }

    /**
     * 生成向量
     * 
     * @param countList
     * @return
     */
    public static List<Vector> generateVector(List<Integer> countList) {
        List<Vector> vectors = new ArrayList<>();
        for (int count : countList) {
            vectors.add(get((double) count));
        }
        return vectors;
    }

    public static Vector get(Double count) {
        DenseVector vector = null;
        if (count == 0)
            vector = new DenseVector(new double[] { 0d, 0d });
        else
            vector = new DenseVector(new double[] { 0d, (double) count });
        return vector;
    }

    /**
     * 获得一个UUID
     * 
     * @return String UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        // 去掉“-”符号
        return uuid.replaceAll("-", "");
    }

}
