package com.stu.oyy.util;

import android.os.Bundle;
import android.os.Message;
import com.stu.oyy.web.GeneralHandle;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2020/12/8 19:58
 * @Description:供子线程调用，需要向主线程发送message
 */
public class MessageUtil {

    private MessageUtil() {
    }

    /**
     * 向主线程handle发送一个message
     *
     * @param message
     */
    public static void sendMessage(Message message) {
        GeneralHandle.getInstance().sendMessage(message);
    }

    /**
     * 向主线程handle发送一个空的message
     *
     * @param what
     */
    public static void sendEmptyMessage(int what) {
        GeneralHandle.getInstance().sendEmptyMessage(what);
    }

    /**
     * 向主线程handle发送一个message，参数为bundle
     *
     * @param what
     * @param bundle
     */
    public static void sendBundle(int what, Bundle bundle) {
        Message message = new Message();
        message.what = what;
        message.setData(bundle);
        MessageUtil.sendMessage(message);
    }

}
