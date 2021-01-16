package com.stu.oyy.web;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.ByteArrayInputStream;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/1/16 17:11
 * @Description:
 */
public class ByteRequestBody extends RequestBody {
    private MediaType contentType;
    private byte[] fileBytes;

    public ByteRequestBody(MediaType contentType, byte[] fileBytes) {
        this.contentType = contentType;
        this.fileBytes = fileBytes;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return fileBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) {
        Source source;
        try {
            source = Okio.source(new ByteArrayInputStream(fileBytes));
            Buffer buf = new Buffer();
            for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                sink.write(buf, readCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
