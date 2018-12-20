package com.bjtu.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件
 * 
 * @author apple
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static ConcurrentHashMap<String, Properties> PROPS_HOLDER = new ConcurrentHashMap<>();

    private PropertiesUtil() {

    }

    public static Properties loadProps(String file) {
        Properties properties = PROPS_HOLDER.get(file);
        try {
            if (properties == null) {
                properties = new Properties();
                InputStream inStream = null;
                try {
                    inStream = PropertiesUtil.class.getResourceAsStream(file);
                    if (inStream != null) {
                        properties.load(inStream);
                    }
                    PROPS_HOLDER.putIfAbsent(file, properties);
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("load[" + file + "] error", e);
        }
        return properties;
    }

    public static Properties getProperties(String file) {
        return loadProps(file);
    }

    public static String getProperty(String file, String key, String defaultValue) {
        Properties properties = loadProps(file);
        return properties.getProperty(key, defaultValue);
    }
}
