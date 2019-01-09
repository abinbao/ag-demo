package com.bjtu.canopy;

import java.util.ArrayList;
import java.util.List;

import com.bjtu.model.Point;

/**
 * Canopy
 * 
 * @author apple
 */
public class Canopy {

    private Long canopyId;
    private Point center; // 中心点
    private List<Point> points;

    public static CanopyBuilder aBuiler() {
        return new CanopyBuilder();
    }

    private static class CanopyBuilder {
        private Point center; // 中心点
        private List<Point> points;

        public CanopyBuilder center(Point center) {
            this.center = center;
            return this;
        }

        public CanopyBuilder center(List<Point> points) {
            this.points = points;
            return this;
        }

        public Canopy build() {
            Canopy canopy = new Canopy();
            canopy.setCenter(center);
            canopy.setPoints(points);
            return canopy;
        }

    }

    public Long getCanopyId() {
        return canopyId;
    }

    public void setCanopyId(Long canopyId) {
        this.canopyId = canopyId;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public List<Point> getPoints() {
        if (null == points) {
            points = new ArrayList<>();
        }
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void computeCenter() {
        double x = 0.0;
        double y = 0.0;
        for (Point point : getPoints()) {
            x += point.getX();
            y += point.getY();
        }
        double z = getPoints().size();
        setCenter(new Point(x / z, y / z));
    }

    @Override
    public String toString() {
        return "Canopy [canopyId=" + canopyId + ", center=" + center + ", points=" + points + "]";
    }

}
