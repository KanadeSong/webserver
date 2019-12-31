package com.seater.smartmining.exception;

import java.nio.file.FileSystemNotFoundException;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/7 0007 17:28
 */
public class SmartiningFileNotFountException extends FileSystemNotFoundException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public SmartiningFileNotFountException(String msg) {
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
