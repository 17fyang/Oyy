package com.stu.oyy.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;
import com.stu.oyy.util.Async;
import com.stu.oyy.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现录音
 *
 * @author chenmy0709
 * @version V001R001C01B001
 */
public class AudioRecorder {
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private Status status = Status.STATUS_NO_READY;

    //文件名
    private String fileName;

    //录音文件
    private List<String> filesName = new ArrayList<>();


    /**
     * 类级的内部类，也就是静态类的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用时才会装载，从而实现了延迟加载
     */
    private static class AudioRecorderHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static AudioRecorder instance = new AudioRecorder();
    }

    private AudioRecorder() {
    }

    public static AudioRecorder getInstance() {
        return AudioRecorderHolder.instance;
    }

    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);

        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        this.fileName = fileName;
        status = Status.STATUS_READY;
    }

    /**
     * 创建默认的录音对象
     *
     * @param fileName 文件名
     */
    public void createDefaultAudio(String fileName) {
        createAudio(fileName, AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
    }


    /**
     * 开始录音
     *
     * @param listener 音频流的监听
     */
    public void startRecord(final RecordStreamListener listener) {
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName))
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        if (status == Status.STATUS_START)
            throw new IllegalStateException("正在录音");

        audioRecord.startRecording();

        Async.run(() -> writeDataTOFile(listener));
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public WaveFile stopRecord() {
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY)
            throw new IllegalStateException("录音尚未开始");

        audioRecord.stop();
        status = Status.STATUS_STOP;
        return release();
    }

    /**
     * 释放资源
     */
    public WaveFile release() {
        WaveFile result = null;

        //假如有暂停录音
        if (filesName.size() > 0) {
            List<String> filePaths = new ArrayList<>();
            for (String fileName : filesName) {
                filePaths.add(FileUtil.getPcmFileAbsolutePath(fileName));
            }
            //清除
            filesName.clear();
            //将多个pcm文件转化为wav文件
            result = mergePCMFilesToWAVFile(filePaths);

        } else {
            //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
            //会报空指针 NullPointerException
            // 将单个pcm文件转化为wav文件
            //Log.d("AudioRecorder", "=====makePCMFileToWAVFile======");
            //makePCMFileToWAVFile();
        }


        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        status = Status.STATUS_NO_READY;
        return result;
    }

    /**
     * 取消录音
     */
    public void canel() {
        filesName.clear();
        fileName = null;
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        status = Status.STATUS_NO_READY;
    }


    /**
     * 将音频信息写入文件
     *
     * @param listener 音频流的监听
     */
    private void writeDataTOFile(RecordStreamListener listener) {
        FileOutputStream fos = null;
        try {
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] audiodata = new byte[bufferSizeInBytes];
            int readsize = 0;
            String currentFileName = fileName;
            if (status == Status.STATUS_PAUSE) {
                //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += filesName.size();
            }

            filesName.add(currentFileName);
            File file = new File(FileUtil.getPcmFileAbsolutePath(currentFileName));
            if (file.exists()) file.delete();
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件

            //将录音状态设置成正在录音状态
            status = Status.STATUS_START;
            while (status == Status.STATUS_START) {
                readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                    fos.write(audiodata);

                    //用于拓展业务
                    if (listener != null) {
                        listener.recordOfByte(audiodata, 0, audiodata.length);
                    }
                }
            }

        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();// 关闭写入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private WaveFile mergePCMFilesToWAVFile(final List<String> filePaths) {

        WaveFile waveFile = PcmToWav.mergePCMFilesToWAVFile(filePaths);

        if (waveFile == null)
            throw new IllegalStateException("fail to convert to WavFile Object!!");

//        if (!waveFile.toFile(FileUtil.getWavFileAbsolutePath(fileName)))
//            throw new IllegalStateException("WavFile Object fail to convert to file!!");

        fileName = null;

        return waveFile;

    }

    /**
     * 将单个pcm文件转化为wav文件
     */
    private void makePCMFileToWAVFile() {

        WaveFile waveFile = PcmToWav.makePCMFileToWAVFile(FileUtil.getPcmFileAbsolutePath(fileName), true);

        if (waveFile == null)
            throw new IllegalStateException("fail to convert to WavFile Object!!");

//        if (!waveFile.toFile(FileUtil.getWavFileAbsolutePath(fileName)))
//            throw new IllegalStateException("WavFile Object fail to convert to file!!");

        fileName = null;

    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取本次录音文件的个数
     *
     * @return
     */
    public int getPcmFilesCount() {
        return filesName.size();
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

}