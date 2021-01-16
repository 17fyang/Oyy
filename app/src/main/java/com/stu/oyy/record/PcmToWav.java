package com.stu.oyy.record;

import android.util.Log;

import java.io.*;
import java.util.List;

/**
 * @author chenmy0709
 * @version V001R001C01B001
 */
public class PcmToWav {
    /**
     * 合并多个pcm文件为一个wav文件
     *
     * @param filePathList pcm文件路径集合
     * @return true|false
     */
    public static WaveFile mergePCMFilesToWAVFile(List<String> filePathList) {
        //合成所有的pcm文件的数据，写到目标文件
        try {
            ByteArrayOutputStream contentByteStream = new ByteArrayOutputStream();
            int contentSize = 0;

            int len;
            byte[] buffer = new byte[1024 * 4];
            for (String path : filePathList) {
                File pcmFile = new File(path);
                InputStream inStream = new FileInputStream(pcmFile);

                //统计所有的pcm文件长度
                contentSize += pcmFile.length();

                //写入输出流
                while ((len = inStream.read(buffer)) != -1)
                    contentByteStream.write(buffer, 0, len);

                inStream.close();
            }
            contentByteStream.close();

            //清理pcm文件
            clearFiles(filePathList);

            WaveHeader header = WaveHeader.buildDefault(contentSize);
            return new WaveFile(header, contentByteStream.toByteArray());

        } catch (IOException e) {
            Log.e("PcmToWav", e.getMessage());
            return null;
        }

    }

    /**
     * 将一个pcm文件转化为wav文件
     *
     * @param pcmPath       pcm文件路径
     * @param deletePcmFile 是否删除源文件
     * @return
     */
    public static WaveFile makePCMFileToWAVFile(String pcmPath, boolean deletePcmFile) {
        File file = new File(pcmPath);
        if (!file.exists()) return null;
        int contentSize = (int) file.length();

        try {
            WaveHeader header = WaveHeader.buildDefault(contentSize);

            //合成所有的pcm文件的数据，写到目标文件
            InputStream inStream = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream contentByteStream = new ByteArrayOutputStream(contentSize);

            byte[] buffer = new byte[1024 * 4];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                contentByteStream.write(buffer, 0, len);
            }

            inStream.close();
            contentByteStream.close();

            //是否删除pcm文件
            if (deletePcmFile) file.delete();

            return new WaveFile(header, contentByteStream.toByteArray());
        } catch (IOException e) {
            Log.e("PcmToWav", e.getMessage());
            return null;
        }
    }

    /**
     * 清除文件
     *
     * @param filePathList
     */
    private static void clearFiles(List<String> filePathList) {
        for (int i = 0; i < filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
