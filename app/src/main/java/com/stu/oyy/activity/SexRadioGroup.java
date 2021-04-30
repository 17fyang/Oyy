package com.stu.oyy.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.stu.oyy.R;
import com.stu.oyy.service.IdentifyService;
import com.stu.oyy.util.Async;
import com.stu.oyy.util.MessageUtil;
import com.stu.oyy.web.GeneralHandle;
import com.stu.oyy.web.MessageKey;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/4/30 17:58
 * @Description:
 */
public class SexRadioGroup extends RadioGroup {
    private RadioButton manButton;
    private RadioButton womanButton;


    public SexRadioGroup(Context context) {
        super(context);
    }

    public SexRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        //返回查询匹配结果
        GeneralHandle.getInstance().registerHandle(MessageKey.IDENTIFY_SEX, (context, msg) -> {
            int result = msg.arg1;
            if (result == 0)
                womanButton.setChecked(true);
            else
                manButton.setChecked(true);
        });
    }

    /**
     * 设置输入，发送匹配对应的选项
     *
     * @param word
     */
    public void setInput(String word) {
        Async.run(() -> {
            boolean result = IdentifyService.getInstance().identifySex(word);
            Message msg = new Message();
            msg.what = MessageKey.IDENTIFY_SEX;
            msg.arg1 = result ? 1 : 0;
            MessageUtil.sendMessage(msg);
        });
    }

    public void setActivity(Activity activity) {
        manButton = activity.findViewById(R.id.rb_1);
        womanButton = activity.findViewById(R.id.rb_2);
    }


}
