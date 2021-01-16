package com.stu.oyy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.stu.oyy.R;
import com.stu.oyy.record.AudioRecorder;
import com.stu.oyy.record.WaveFile;
import com.stu.oyy.service.TranslateService;
import com.stu.oyy.util.Async;
import com.stu.oyy.util.MessageUtil;
import com.stu.oyy.web.GeneralHandle;
import com.stu.oyy.web.MessageKey;


public class MainActivity extends AppCompatActivity {
    //申请权限
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_ALL = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private TextView mainTextView;
    private TextView buttonStatusTextView;
    private Button button;

    {
        //注册修改主textview翻译结果的handle
        GeneralHandle.getInstance().registerHandle(MessageKey.SET_TRANSLATE_TEXT, (context, msg) -> {
            String[] result = (String[]) msg.obj;

            StringBuilder sb = new StringBuilder();
            for (String s : result) sb.append(s).append("\r\n");

            mainTextView.setText(sb.toString());
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStatusTextView = findViewById(R.id.textView2);
        mainTextView = findViewById(R.id.textView);

        initButton();
        verifyPermissions(this);

    }


    /**
     * 初始化录音按钮
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initButton() {
        button = findViewById(R.id.button);
        button.setOnTouchListener((view, event) -> {
            if (event.getAction() == 0) {
                buttonStatusTextView.setText("录音状态：录音中");
                AudioRecorder.getInstance().createDefaultAudio("test");
                AudioRecorder.getInstance().startRecord(null);
            } else if (event.getAction() == 1) {
                buttonStatusTextView.setText("录音状态：未开始");
                WaveFile waveFile = AudioRecorder.getInstance().stopRecord();

                //http请求语音识别，切换子线程
                Async.run(() -> {
                    String[] result = TranslateService.getInstance().translate(waveFile);
                    Message msg = new Message();
                    msg.what = MessageKey.SET_TRANSLATE_TEXT;
                    msg.obj = result;
                    MessageUtil.sendMessage(msg);
                });
            }

            return true;
        });
    }


    /**
     * 申请录音权限
     */
    public static void verifyPermissions(Activity activity) {
        boolean permission = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);

        if (permission)
            ActivityCompat.requestPermissions(activity, PERMISSION_ALL, GET_RECODE_AUDIO);

    }
}