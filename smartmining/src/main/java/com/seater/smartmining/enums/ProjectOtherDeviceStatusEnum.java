package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/11 0011 11:15
 */
public enum ProjectOtherDeviceStatusEnum {
    Unknow(0,"未知"),
    Working(1,"开机"),
    Stop(2,"停机"),
    WoekRequest(3,"开机申请"),
    StopRequest(4,"停机申请"),
    Fault(5,"故障"),
    NotUse(6,"停用");

    private Integer alias;
    private String value;
    ProjectOtherDeviceStatusEnum(Integer alias, String value) {
        this.alias = alias;
        this.value = value;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
