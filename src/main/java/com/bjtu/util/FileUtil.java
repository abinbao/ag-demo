package com.bjtu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjtu.config.Config;
import com.bjtu.model.Point;
/**
 * 读取样本数据
 */
public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class); 


	public static List<Point> loadata() {
		logger.info("======Starting Load Data=======");
		List<Point> list = new ArrayList<Point>();

		File file = new File(Config.DATA_PATH);

		BufferedReader reader = null;
		String temp = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			int index = 1;
			while ((temp=reader.readLine()) != null) {
				
				if(index < Config.startNum) {
					index++;
					continue;
				}
				String[] args = temp.split("\t");
				double x = Double.parseDouble(args[3]);
				double y = Double.parseDouble(args[2]);
				
				Point p = new Point(x,y);
//				logger.info(p.toString() + index);
				
				list.add(p);
				if(Config.readNum == index)
					break;
				index++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("数据加载结束");
		return list;
	}
}
