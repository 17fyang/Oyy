package com.stu.oyy.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.util.AttributeSet;
import com.stu.oyy.R;
import com.stu.oyy.service.IdentifyService;
import com.stu.oyy.util.Async;
import com.stu.oyy.util.MessageUtil;
import com.stu.oyy.web.GeneralHandle;
import com.stu.oyy.web.MessageKey;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/4/30 18:29
 * @Description:
 */
public class ProvinceSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    private ProvinceAdapter provinceAdapter;

    public ProvinceSpinner(Context context) {
        super(context);
    }

    public ProvinceSpinner(Context context, int mode) {
        super(context, mode);
    }

    public ProvinceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProvinceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProvinceSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public ProvinceSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    {
        //返回查询匹配结果
        GeneralHandle.getInstance().registerHandle(MessageKey.IDENTIFY_PROVINCE, (context, msg) -> {
            int result = msg.arg1;
            System.out.println("dasdsadsad" + result);
            this.setSelection(result);
        });
    }

    public void setInput(String word) {
        Async.run(() -> {
            int index = IdentifyService.getInstance().identifyProvince(word);
            Message msg = new Message();
            msg.what = MessageKey.IDENTIFY_PROVINCE;
            msg.arg1 = index;
            MessageUtil.sendMessage(msg);
        });
    }

    /**
     * 初始化操作
     */
    public void init() {
        provinceAdapter = new ProvinceAdapter(R.layout.item_province);
        this.setAdapter(provinceAdapter);
    }
}
