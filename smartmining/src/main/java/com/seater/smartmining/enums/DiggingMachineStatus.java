package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/4 0004 11:43
 */
public enum DiggingMachineStatus {

    Unknow(0, "未知"),
    Working(1,"开机"),
    Stop(2,"停机"),
    WoekRequest(3,"开机申请"),
    StopRequest(4,"停机申请");

    private String value;

    private Integer alias;
    DiggingMachineStatus(Integer alias, String value) {
        this.alias = alias;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
