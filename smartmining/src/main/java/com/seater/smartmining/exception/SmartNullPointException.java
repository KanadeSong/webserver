package com.seater.smartmining.exception;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/15 0015 17:53
 */
public class SmartNullPointException extends NullPointerException{
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public SmartNullPointException(String msg) {
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
