package com.bjtu.pipeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.model.Point;
import com.bjtu.util.PropertiesUtil;

public class StationInfoDao {
    private static final Logger LOG = LoggerFactory.getLogger(StationInfoDao.class);
    private static Properties prop = PropertiesUtil.loadProps("/config.properties");

    private static final String SQL = "select longitude,latitude from gaode_location_info";

    protected static Connection getConn() {
        String driver = prop.getProperty("mysql.driver");
        String url = prop.getProperty("mysql.url");
        String username = prop.getProperty("mysql.username");
        String password = prop.getProperty("mysql.password");
        Connection conn = null;
        try {
            Class.forName(driver); // classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (SQLException e1) {
            LOG.error(e1.getMessage());
        }
        return conn;
    }

    public static List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        Connection conn = getConn();
        PreparedStatement pstmt = null;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Point point = new Point();
                point.setY(Double.parseDouble(rs.getString(1)));
                point.setX(Double.parseDouble(rs.getString(2)));
                points.add(point);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                conn.close();
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        }
        return points;
    }

    public static void main(String[] args) {
        getPoints();
    }
}
