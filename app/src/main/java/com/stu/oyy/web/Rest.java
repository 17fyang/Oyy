package com.stu.oyy.web;

/**
 * @Author: 乌鸦坐飞机亠
 * @CreateDate: 2020/11/12 10:01
 * @Version: 1.0
 * @Description: Restful格式返回封装类
 */
public class Rest {
    private int ok;
    private String failed;
    private int err_no;
    private String data;

    public Rest() {
    }

    public Rest(int ok, String failed, int err_no, String data) {
        this.ok = ok;
        this.failed = failed;
        this.err_no = err_no;
        this.data = data;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public String getFailed() {
        return failed;
    }

    public void setFailed(String failed) {
        this.failed = failed;
    }

    public int getErr_no() {
        return err_no;
    }

    public void setErr_no(int err_no) {
        this.err_no = err_no;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Rest{" +
                "ok=" + ok +
                ", failed='" + failed + '\'' +
                ", err_no=" + err_no +
                ", data='" + data + '\'' +
                '}';
    }
}