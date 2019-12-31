package com.seater.smartmining.exception;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/8 0008 9:22
 */
public class SytechPackSelectFailExcption extends Exception{

    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public SytechPackSelectFailExcption(){
        super();
        this.msg = "上传文件选择错误";
    }

    public SytechPackSelectFailExcption(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
