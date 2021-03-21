package com.stu.oyy.util;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/3/21 16:02
 * @Description:
 */
public class StringUtil {

    public static String trimChinese(String s) {
        s = s.replaceAll("[，。！：]+", "");
        return s.replaceAll("[^\\u4E00-\\u9FFF0-9a-zA-Z]+", "");
    }
}
