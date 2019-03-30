package com.bjtu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.config.Config;
import com.bjtu.kmeans.KmeansCluster;
import com.bjtu.model.MergeSquare;
import com.bjtu.model.Point;
import com.bjtu.model.Square;
import com.bjtu.pipeline.StationInfoDao;
import com.bjtu.util.CalUtil;
import com.bjtu.util.FileUtil;
import com.bjtu.util.PropertiesUtil;
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
    private static Properties prop = PropertiesUtil.loadProps("/config.properties");

    private static double len = 320.0;
    private static double height = 160.0;
    private static double unit = 1;

    public static void main(String[] args) {

        /*
         * 数据处理层： 1. 数据格式化处理 2. 数据归一化 3. 数据格式转换
         */
        // 1. 首先读取点的个数

        List<Point> pointList = null;
        if (Boolean.parseBoolean(prop.getProperty("chekin.enabled")))
            pointList = FileUtil.loadata();
        else
            pointList = StationInfoDao.getPoints();
        logger.info(" === >>> 共读取：{} 个点", pointList.size());
        /*
         * 函数逻辑处理层：1. 区域划分 2. 区域数据点集合统计
         */
        // 2. 划分区域
        Map<String, Square> squareMap = new HashMap<>();
        List<Square> squareList = SquareUtils.divideSquare(len, height, unit, squareMap);
        logger.info(" === >>> 共划分区域：{} 个", squareList.size());
        // 3. 统计每个区域中点的个数
        CalUtil.calPointNum(squareList, pointList);
        logger.info(" === >>> 区域统计结束 <<< === ");
        ArrayList<Double> countList = new ArrayList<>(); // 统计点的集合
        // 打印 划分好的网格中 count 大于 0 的区域
        for (Square square : squareList) {
            if (square.getCount() > 0) {
                logger.info("x1:" + square.getX1() + ", x2:" + square.getX2() + ", y1:" + square.getY1() + ", y2:"
                        + square.getY2() + ", count:" + square.getCount());
            }
            countList.add((double) square.getCount()); // 向点的集合中添加点
        }
        // 4. 使用 Cannopy 算法 结合 Kmeans 算法构建簇
        // 4.1 生成点的向量
        List<Point> vectors = SquareUtils.generateVector(countList);
        // 4.2 对点的向量进行聚类
        List<KmeansCluster> clusters = CanopyWithKmeansCluster.runCluster(vectors);
        // 5. 对区域进行打标签，判断集合属于哪个簇
        List<Future> futures = new ArrayList<>();
        cdl = new CountDownLatch(squareList.size());
        for (Square squ : squareList) {
            futures.add(EXECUTOR_SERVICE.submit(new Task(squ, clusters)));
        }
        // 插入数据完成后 执行修改操作
        try {
            cdl.await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(" === >>> 区域 打标签完成 <<< === ");
        EXECUTOR_SERVICE.shutdown();
        // 6. 对区域进行遍历
        Set<Square> set = new HashSet<>(); // 判断该区域是否被合并
        Map<String, MergeSquare> mergeMap = new ConcurrentHashMap<>(clusters.size());
        for (Square square : squareList) {
            logger.info("当前区域:{}", square.toString());
            if (mergeMap.isEmpty()) {
                logger.info("还没有合并区域,开始构建 ===>>> CanopyId:{}", square.getCanopyId());
                set.add(square);
                buildMergeSquare(square, mergeMap);
                handleAdjoinSuqare(square, set, mergeMap, squareMap);
            } else {
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
        // =============开始计算===================
        logger.info("==========>>>>>>>开始统计合并区域信息<<<<<<<<<=============");
        Map<String, Double> mergequareMap = new HashMap<>(); // 保存每个合并区域的区域面积
        Map<String, Integer> mergeSquarePointMap = new HashMap<>(); // 保存每个合并区域的点的总数
        Map<String, Double> mergeSquareLapPointMap = new HashMap<>(); // 保存每个合并区域的噪声点的总数
        Map<String, Double> querySquareMap = new HashMap<>(); // 合并后的区域在查询区域中的面积
        for (Square square : squareList) {
            CalUtil.searchLapalcePointNumByCluster(square, querySquareMap, Config.querySquare);
            String mergeId = square.getMergeId();
            if (!mergequareMap.containsKey(mergeId)) {
                mergequareMap.put(mergeId, square.getSquare());
                mergeSquarePointMap.put(mergeId, square.getCount());
                mergeSquareLapPointMap.put(mergeId, square.getCountLa());
            } else {
                mergequareMap.put(mergeId, mergequareMap.get(mergeId) + square.getSquare());
                mergeSquarePointMap.put(mergeId, mergeSquarePointMap.get(mergeId) + square.getCount());
                mergeSquareLapPointMap.put(mergeId, mergeSquareLapPointMap.get(mergeId) + square.getCountLa());
            }
            logger.info(square.toString() + " CanopyId:" + square.getCanopyId() + " mergeId:" + square.getMergeId());
        }

        // 最后开始计算
        double result = 0d;
        for (Entry<String, Double> entry : querySquareMap.entrySet()) {
            String key = entry.getKey();
            logger.info("=======>>>>>>>合并区域ID:" + key + " , 面积为：" + mergequareMap.get(key) + "<<<<<<=========");
            double rate = entry.getValue() / mergequareMap.get(key);
            logger.info("=======>>>>>>>合并区域ID:" + key + " , 查询区域占比：" + rate + "<<<<<<=========");
            logger.info("=======>>>>>>>合并区域ID:" + key + " , 点的个数为：" + mergeSquarePointMap.get(key) + "<<<<<<=========");
            double count = mergeSquarePointMap.get(key) * rate
                    + CalUtil.lapalceNoice(mergeSquarePointMap.get(key), 0.1);
            result = result + count;
        }
        int agActNum = CalUtil.searchActualPointNum(Config.querySquare, pointList);
        logger.info("=====>>>>>实际查询点的个数：" + agActNum + " <<<<<<=======");
        logger.info("=====>>>>>本算法查询点的个数：" + result + " <<<<<<=======");

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
            String skey = square.getX1() + "_" + square.getX2() + "_" + square.getY1() + "_" + square.getY2();
            if (set.contains(adjoin) && square.getCanopyId().equals(squareMap.get(key).getCanopyId())) {
                logger.info(" === >>> 当前区域 key : {} square CanopyId:{} <<< === ", skey, square.getCanopyId());
                logger.info(" === >>> 当前相邻区域 adjoin key : {} CanopyId:{} has been merged <<< === ", key,
                        squareMap.get(key).getCanopyId());
                square.setMergeId(squareMap.get(key).getMergeId());
            }
        }
        for (Square adjoin : square.getAdjoinList()) {
            String key = adjoin.getX1() + "_" + adjoin.getX2() + "_" + adjoin.getY1() + "_" + adjoin.getY2();
            String skey = square.getX1() + "_" + square.getX2() + "_" + square.getY1() + "_" + square.getY2();
            if (!set.contains(adjoin) && square.getCanopyId().equals(squareMap.get(key).getCanopyId())) {
                logger.info("当前区域 key : {} square CanopyId:{} ", skey, square.getCanopyId());
                logger.info("当前相邻区域 adjoin key : {} CanopyId:{} will be merged <<< ===", key,
                        squareMap.get(key).getCanopyId());
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
        private List<KmeansCluster> clusters;

        public Task(Square squ, List<KmeansCluster> clusters) {
            this.square = squ;
            this.clusters = clusters;
        }

        @Override
        public Boolean call() throws Exception {
            Point point = new Point(0d, (double) square.getCount());
            for (KmeansCluster cluster : clusters) {
                boolean flag = cluster.coverCluster(point);
                if (flag) {
                    square.setCanopyId(String.valueOf(cluster.getClusterId()));
                }
                cdl.countDown();
            }
            return true;
        }

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
