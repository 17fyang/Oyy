package com.stu.oyy.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/1/16 12:40
 * @Description:wav格式文件,这样封装不适应wav文件太大的情况
 */
public class WaveFile {
    private WaveHeader head;
    private byte[] content;

    public WaveFile(WaveHeader head, byte[] content) {
        this.head = head;
        this.content = content;
    }

    /**
     * 写入到文件磁盘中
     *
     * @param fileName
     * @return
     */
    public boolean toFile(String fileName) {
        try (OutputStream out = new FileOutputStream(new File(fileName));) {
            byte[] headByte = head.getHeader();

            // WAV标准，头部应该是44字节,如果不是44个字节则不进行转换文件
            if (headByte.length != 44) return false;

            out.write(headByte);
            out.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * wavFile转成byte数组
     *
     * @return
     */
    public byte[] toByteArray() {
        try {
            byte[] headByte = head.getHeader();

            // WAV标准，头部应该是44字节,如果不是44个字节则不进行转换文件
            if (headByte.length != 44) return null;

            byte[] result = new byte[headByte.length + content.length];
            System.arraycopy(headByte, 0, result, 0, headByte.length);
            System.arraycopy(content, 0, result, headByte.length, content.length);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WaveHeader getHead() {
        return head;
    }

    public void setHead(WaveHeader head) {
        this.head = head;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
