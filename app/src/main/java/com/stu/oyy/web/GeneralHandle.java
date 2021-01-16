package com.stu.oyy.web;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2020/12/8 18:50
 * @Description:
 */
public class GeneralHandle extends Handler {
    private static GeneralHandle instance;
    private Context curContext;
    private Map<Integer, IHandlerConsumer> consumerMap = new ConcurrentHashMap<>();

    public static GeneralHandle getInstance() {
        if (instance == null) instance = new GeneralHandle(Looper.myLooper());
        return instance;
    }

    private GeneralHandle(Looper looper) {
        super(looper);
        registerConsumer();
    }

    /**
     * 注册全局的consumerHandle，也可以在其他地方注册
     */
    private void registerConsumer() {
        //注册toast
        this.registerHandle(MessageKey.TOAST, (context, msg) -> Toast.makeText(this.curContext, msg.getData().getString("info"), Toast.LENGTH_SHORT).show());

    }

    /**
     * 处理handle接收到的msg，先查询已注册的consumer，然后交给consumer处理
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        IHandlerConsumer consumer = consumerMap.get(msg.what);
        if (consumer != null) {
            consumer.handle(curContext, msg);
        } else {
            Log.e("system", "consumer has no register by key:" + msg.what);
        }
    }

    /**
     * 注册一个Consumer和对应的key
     * <p>
     * 注意：注册方法对于每个key只调用一次，所以IHandlerConsumer类的变量内容在创建时就固定了
     * 如果是用内部类或者lambda的方式传值，且使用到了外部变量，则可能会出现结果和预期不符的情况
     *
     * @param what
     * @param iHandlerConsumer
     */
    public void registerHandle(int what, IHandlerConsumer iHandlerConsumer) {
        if (!consumerMap.containsKey(what))
            consumerMap.put(what, iHandlerConsumer);
        else
            Log.e("init", "register handle by conflicting key " + what);
    }

    /**
     * 判断某个key是否已经被注册
     *
     * @param key
     * @return
     */
    public boolean hasKey(int key) {
        return consumerMap.containsKey(key);
    }

    /**
     * 设置当前的activity，仅activity切换时调用
     *
     * @param context
     */
    public void setCurContext(Context context) {
        this.curContext = context;
    }
}
