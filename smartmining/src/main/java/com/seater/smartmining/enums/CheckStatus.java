package com.seater.smartmining.enums;

/**
 * @Description 设备检查状态
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:34
 */
public enum CheckStatus {
    UnCheck(0,"UnCheck"),   //  未检查
    Checked(1,"Checked");   //  已检查

    private String value;
    private Integer code;
    CheckStatus(Integer code,String value)
    {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return this.value;
    }

}
