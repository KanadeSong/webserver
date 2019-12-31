package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/9 0009 15:00
 */
public enum AutoScheduleRequestEnum {

    UNKNOW(0, "未知"),
    DIGGINGMACHINE(1, "挖机参数"),
    CAR(2, "渣车参数"),
    WORKINFO(3, "工作信息");

    private Integer value;
    private String name;

    AutoScheduleRequestEnum(Integer value, String name){
        this.value = value;
        this.name = name;
    }

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

    @Override
    public String toString()
    {
        return this.name;
    }
}
