package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/9 0009 15:09
 */
public enum AutoScheduleRequestTypeEnum {

    UNKNOW(0, "未知"),
    DIGGINGMACHINE(1, "挖机自动分配"),
    SLAGSITE(2, "渣场自动分配");

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer value;
    private String name;

    AutoScheduleRequestTypeEnum(Integer value, String name){
        this.value = value;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
