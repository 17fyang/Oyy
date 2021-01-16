package com.stu.oyy.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 管理录音文件的类
 *
 * @author chenmy0709
 * @version V001R001C01B001
 */
public class FileUtil {

    //工作空间路径
    public static final String ROOT_PATH = "/storage/emulated/0/workspace/oyy";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASE_PATH = ROOT_PATH + "/pcm";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASE_PATH = ROOT_PATH + "/wav";

    static {
        //初始化创建文件夹
        File pcmDirs = new File(AUDIO_PCM_BASE_PATH);
        if (!pcmDirs.exists()) pcmDirs.mkdirs();

        File wavDirs = new File(AUDIO_WAV_BASE_PATH);
        if (!wavDirs.exists()) wavDirs.mkdirs();
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) throw new NullPointerException("fileName isEmpty");
        if (isExitSdcard()) throw new IllegalStateException("sd card no found");

        if (!fileName.startsWith("/")) fileName = "/" + fileName;
        if (!fileName.endsWith(".pcm")) fileName = fileName + ".pcm";

        return AUDIO_PCM_BASE_PATH + fileName;
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) throw new NullPointerException("fileName can't be null");
        if (isExitSdcard()) throw new IllegalStateException("sd card no found");

        if (!fileName.startsWith("/")) fileName = "/" + fileName;
        if (!fileName.endsWith(".wav")) fileName = fileName + ".wav";

        return AUDIO_WAV_BASE_PATH + fileName;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    private static boolean isExitSdcard() {
        return !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
