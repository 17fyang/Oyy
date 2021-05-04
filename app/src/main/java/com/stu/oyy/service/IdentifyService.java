package com.stu.oyy.service;

import com.stu.oyy.util.HttpUtil;
import com.stu.oyy.web.Rest;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/4/30 17:44
 * @Description:
 */
public class IdentifyService {
    public static final String SERVER_LOCATION = "http://120.79.175.145:8099";
    public static final String IDENTIFY_SEX = SERVER_LOCATION + "/identifySex?word=%s";
    public static final String IDENTIFY_PROVINCE = SERVER_LOCATION + "/identifyProvince?province=%s";

    private static class IdentifyServiceHolder {
        public static IdentifyService instance = new IdentifyService();
    }

    public static IdentifyService getInstance() {
        return IdentifyServiceHolder.instance;
    }

    /**
     * 验证语言输入的性别，返回true为男性，false为女性
     *
     * @param word
     * @return
     */
    public boolean identifySex(String word) {
        String url = String.format(IDENTIFY_SEX, word);
        Rest result = HttpUtil.syncGet(url);
        if (result == null) return false;
        return result.getData().equals("1");
    }

    /**
     * 验证语音输入的省份，返回省份数组的下标位置
     *
     * @param word
     * @return
     */
    public int identifyProvince(String word) {
        String url = String.format(IDENTIFY_PROVINCE, word);
        Rest result = HttpUtil.syncGet(url);
        if (result == null) return 0;
        return Integer.parseInt(result.getData());
    }


}
