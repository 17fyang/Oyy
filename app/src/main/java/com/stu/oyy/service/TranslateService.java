package com.stu.oyy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stu.oyy.record.WaveFile;
import com.stu.oyy.util.EncryptUtil;
import com.stu.oyy.util.HttpUtil;
import com.stu.oyy.util.SliceIdGenerator;
import com.stu.oyy.web.ByteRequestBody;
import com.stu.oyy.web.Rest;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Iterator;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/1/16 14:42
 * @Description:
 */
public class TranslateService {

    //讯飞云id
    public static final String APPID = "6056fe82";
    public static final String SECRET_KEY = "4a6ca083d73d5ed4756ae9490ad45aa8";

    //讯飞云api
    public static final String BASE_API = "http://raasr.xfyun.cn/api";
    public static final String PREPARE = BASE_API + "/prepare";
    public static final String UPLOAD = BASE_API + "/upload";
    public static final String MERGE = BASE_API + "/merge";
    public static final String GET_RESULT = BASE_API + "/getResult";
    public static final String GET_PROGRESS = BASE_API + "/getProgress";

    //文件分片大小,可根据实际情况调整
    public static final int SLICE_SICE = 10 * 1024 * 1024;

    //重试次数
    public static final int RETRY_TIMES = 5;
    //重试间隔（ms）
    public static final int RETRY_INTERVAL = 2000;

    /**
     * 懒加载的单例模式
     */
    private static class TranslateServiceHolder {
        private static TranslateService instance = new TranslateService();
    }

    public static TranslateService getInstance() {
        return TranslateServiceHolder.instance;
    }

    /**
     * 将wavFile对象识别成文字,默认不分片
     *
     * @param waveFile
     * @return
     */
    public String[] translate(WaveFile waveFile) {
        try {
            byte[] fileByte = waveFile.toByteArray();

            //预处理
            String taskId = prepare(fileByte);

            //发送分片数据
            SliceIdGenerator generator = new SliceIdGenerator();
            uploadSlice(taskId, generator.getNextSliceId(), fileByte);

            //合并
            merge(taskId);

            //查询进度
            int retry = 0;
            while (!getProgress(taskId) && retry < RETRY_TIMES) {
                retry++;
                Thread.sleep(RETRY_INTERVAL * retry);
            }

            //查询结果
            return getResult(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{""};
        }
    }

    /**
     * 预处理
     *
     * @return
     * @throws SignatureException
     */
    private String prepare(byte[] FileByte) {
        FormBody.Builder body = this.buildPublicFormBody();

        long fileLength = FileByte.length;

        body.add("file_len", fileLength + "");
        body.add("file_name", "test.wav");
        body.add("slice_num", "1");

        /********************TODO 可配置参数********************/
        // 转写类型
//        body.add("lfasr_type", "0");
        // 开启分词
//        body.add("has_participle", "true");
        // 说话人分离
//        body.add("has_seperate", "true");
        // 设置多候选词个数
//        body.add("max_alternatives", "2");
        /****************************************************/

        Rest response = HttpUtil.syncPost(PREPARE, body.build());
        System.out.println("prepare:" + response);
        if (response == null || response.getOk() != 0) throw new RuntimeException("预处理接口请求失败！");


        return response.getData();
    }

    /**
     * 分片上传
     *
     * @param taskId 任务id
     * @param slice  分片的byte数组
     * @throws SignatureException
     */
    private void uploadSlice(String taskId, String sliceId, byte[] slice) {
        MultipartBody.Builder body = this.buildPublicMultipartBody();
        body.setType(MultipartBody.FORM);
        body.addFormDataPart("content", "dsad", new ByteRequestBody(MultipartBody.FORM, slice));
        body.addFormDataPart("task_id", taskId);
        body.addFormDataPart("slice_id", sliceId);
        body.addFormDataPart("content", new String(slice, 0, slice.length, StandardCharsets.US_ASCII));

        //设置请求头
        Request request = new Request.Builder()
                .url(UPLOAD)
                .post(body.build())
                .header("Content-Type", "multipart/form-data;")
                .build();

        Rest response = HttpUtil.syncPost(request);

        System.out.println("upload:" + response);
        if (response == null || response.getOk() != 0) throw new RuntimeException("分片上传失败！");

    }


    /**
     * 文件合并
     *
     * @param taskId 任务id
     * @throws SignatureException
     */
    private void merge(String taskId) {
        RequestBody body = this.buildPublicFormBody().add("task_id", taskId).build();
        Rest response = HttpUtil.syncPost(MERGE, body);
        System.out.println("merge:" + response);
        if (response == null || response.getOk() != 0) throw new RuntimeException("文件合并接口请求失败！");

    }

    /**
     * 获取任务进度
     *
     * @param taskId 任务id
     * @throws SignatureException
     */
    private boolean getProgress(String taskId) {
        RequestBody body = this.buildPublicFormBody().add("task_id", taskId).build();
        Rest response = HttpUtil.syncPost(GET_PROGRESS, body);
        System.out.println("progress:" + response);
        if (response == null || response.getOk() != 0) throw new RuntimeException("文件合并接口请求失败！");


        JSONObject responseJson = (JSONObject) JSON.parse(response.getData());
        int taskStatus = responseJson.getInteger("status");

        return response.getOk() == 0 && taskStatus == 9;
    }

    /**
     * 获取转写结果
     *
     * @param taskId
     * @return
     * @throws SignatureException
     */
    private String[] getResult(String taskId) {
        RequestBody body = this.buildPublicFormBody().add("task_id", taskId).build();
        Rest response = HttpUtil.syncPost(GET_RESULT, body);
        System.out.println("result:" + response);
        if (response == null || response.getOk() != 0) throw new RuntimeException("获取结果接口请求失败！");

        JSONArray jsonArray = JSONArray.parseArray(response.getData());

        String[] result = new String[jsonArray.size()];
        Iterator<Object> it = jsonArray.iterator();
        int i = 0;
        while (it.hasNext()) {
            JSONObject jsonObject = (JSONObject) it.next();
            result[i++] = jsonObject.getString("onebest");
        }
        return result;
    }

    /**
     * 生成一个FormBody，带有讯飞云api的公共必要参数
     *
     * @return
     */
    private FormBody.Builder buildPublicFormBody() {
        String ts = String.valueOf(System.currentTimeMillis() / 1000L);
        String signature = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(APPID + ts), SECRET_KEY);

        return new FormBody.Builder()
                .add("app_id", APPID)
                .add("ts", ts)
                .add("signa", signature);
    }

    /**
     * 生成一个MultipartBody，带有讯飞云api的公共必要参数
     *
     * @return
     */
    private MultipartBody.Builder buildPublicMultipartBody() {
        String ts = String.valueOf(System.currentTimeMillis() / 1000L);
        String signature = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(APPID + ts), SECRET_KEY);

        return new MultipartBody.Builder()
                .addFormDataPart("app_id", APPID)
                .addFormDataPart("ts", ts)
                .addFormDataPart("signa", signature);
    }

}
