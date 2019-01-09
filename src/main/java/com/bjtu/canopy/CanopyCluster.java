package com.bjtu.canopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bjtu.canopy.distance.AbstractDistanceComputor;
import com.bjtu.canopy.distance.EuclideanDistanceComputor;
import com.bjtu.model.Point;

/**
 * Canopy 聚类
 * 
 * @author apple
 */
public class CanopyCluster {

    private double T1 = 8d;
    private double T2 = 4d;
    private List<Point> points;
    private List<Canopy> canopies;
    private AbstractDistanceComputor distanceComputer;

    public CanopyCluster() {
        init();
    }

    /**
     * 返回 Canopy 集合
     * 
     * @return
     */
    public List<Canopy> getCanopies() {
        return canopies;
    }

    public CanopyCluster(Double t1, Double t2, List<Point> points) {
        this(t1, t2, points, new EuclideanDistanceComputor());
    }

    public CanopyCluster(Double t1, Double t2, List<Point> points, AbstractDistanceComputor distanceComputer) {
        T1 = t1;
        T2 = t2;
        this.points = points;
        this.distanceComputer = distanceComputer;
    }

    public CanopyCluster(List<Point> points) {
        this.points = points;
    }

    public void init() {
        points = new ArrayList<Point>();
        points.add(new Point(8.1, 8.1));
        points.add(new Point(7.1, 7.1));
        points.add(new Point(6.2, 6.2));
        points.add(new Point(7.1, 7.1));
        points.add(new Point(2.1, 2.1));
        points.add(new Point(1.1, 1.1));
        points.add(new Point(0.1, 0.1));
        points.add(new Point(3.0, 3.0));
        canopies = new ArrayList<Canopy>();
        distanceComputer = new EuclideanDistanceComputor();
    }

    /**
     * 开始Canopy算法
     */
    public void runCluster() {
        while (!points.isEmpty()) {
            Iterator<Point> iter = points.iterator();
            while (iter.hasNext()) {
                Point current = iter.next();
                System.out.println("Current Point : " + current.toString());
                // 取第一个点作为初始的 Canopy
                if (canopies.isEmpty()) {
                    Canopy canopy = new Canopy();
                    canopy.setCanopyId(0l);
                    canopy.setCenter(current);
                    canopy.getPoints().add(current);
                    canopies.add(canopy);
                    iter.remove();
                    continue;
                }
                boolean isRemove = false;
                int index = 0;
                // 遍历 计算当前点到 Canopy 的距离
                for (Canopy canopy : canopies) {
                    Point center = canopy.getCenter();
                    double distance = distanceComputer.computeDistance(current, center);
                    // 距离小于T1加入canopy，打上弱标记
                    if (distance < T1) {
                        current.setMark(Point.MARK_WEAK);
                        canopy.getPoints().add(current);
                    } else if (distance > T1) {
                        index++;
                    }
                    // 距离小于T2则从列表中移除，打上强标记
                    if (distance <= T2) {
                        current.setMark(Point.MARK_STRONG);
                        isRemove = true;
                    }
                }
                // 如果到所有canopy的距离都大于T1,生成新的canopy
                if (index == canopies.size()) {
                    Canopy newCanopy = new Canopy();
                    newCanopy.setCanopyId((long) index);
                    newCanopy.setCenter(current);
                    newCanopy.getPoints().add(current);
                    canopies.add(newCanopy);
                    isRemove = true;
                }
                if (isRemove) {
                    iter.remove();
                }
            }
        }
        // 重新计算中心点
        for (Canopy c : canopies) {
            System.out.println("old center: " + c.getCenter());
            c.computeCenter();
            System.out.println("new center: " + c.getCenter());
            System.out.println("new center: " + c.getPoints());
        }
    }

    /**
     * @param canopy
     * @param p
     * @return
     */
    public boolean canopyCovers(Canopy canopy, Point p) {
        return canopyCovers(canopy, p, new EuclideanDistanceComputor());
    }

    /**
     * @param canopy
     * @param p
     * @param computor
     * @return
     */
    public boolean canopyCovers(Canopy canopy, Point p, AbstractDistanceComputor computor) {
        return canopyCovers(canopy, p, computor, T1);
    }

    /**
     * @param canopy
     * @param p
     * @param computor
     * @param T1
     * @return
     */
    public boolean canopyCovers(Canopy canopy, Point p, AbstractDistanceComputor computor, double T1) {
        Point center = canopy.getCenter();
        double distance = computor.computeDistance(p, center);
        return distance < T1;
    }

    public static void main(String[] args) {
        CanopyCluster builder = new CanopyCluster();
        builder.runCluster();
        builder.getCanopies();
        for (Canopy canopy : builder.getCanopies()) {
            System.out.println(canopy.toString());
        }
        System.out.println(builder.getCanopies().size());
        System.out.println(builder.canopyCovers(builder.getCanopies().get(0), new Point(8.1, 8.1)));
        System.out.println(builder.canopyCovers(builder.getCanopies().get(1), new Point(8.1, 8.1)));

    }
}
