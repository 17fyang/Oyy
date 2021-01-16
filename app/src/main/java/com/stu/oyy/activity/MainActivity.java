package com.stu.oyy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.stu.oyy.R;
import com.stu.oyy.record.AudioRecorder;


public class MainActivity extends AppCompatActivity {
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_ALL = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private TextView buttonStatusTextView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStatusTextView();
        initButton();
        initMedia();
        verifyPermissions(this);

    }

    /**
     * 初始化录音
     */
    private void initMedia() {
        AudioRecorder.getInstance().createDefaultAudio("test");

        //初始化创建文件夹

    }


    /**
     * 初始化录音按钮
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initButton(){
        button=findViewById(R.id.button);
        button.setOnTouchListener((view,event)->{
            if(event.getAction()==0){
                buttonStatusTextView.setText("录音状态：录音中");
                AudioRecorder.getInstance().startRecord(null);
            }else if(event.getAction()==1){
                buttonStatusTextView.setText("录音状态：未开始");
                AudioRecorder.getInstance().stopRecord();
            }

            return true;
        });
    }

    /**
     * 初始化显示按钮状态的textview
     */
    private void initStatusTextView() {
        buttonStatusTextView=findViewById(R.id.textView2);
    }
    /**
     * 申请录音权限
     */
    public static void verifyPermissions(Activity activity) {
        boolean permission = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
        if (permission) {
            ActivityCompat.requestPermissions(activity, PERMISSION_ALL,
                    GET_RECODE_AUDIO);
        }
    }
}