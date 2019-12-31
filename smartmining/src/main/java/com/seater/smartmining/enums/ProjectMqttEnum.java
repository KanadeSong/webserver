package com.seater.smartmining.enums;

/**
 * @Description:终端交互枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 12:04
 */
public enum ProjectMqttEnum {

    Unknow(0, "未知"),
    SlagSiteWork(1, "渣场终端卸载"),
    SlagCarWork(2, "渣车终端装载");

    private String name;

    private Integer value;
    ProjectMqttEnum(Integer value ,String name)
    {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    @Override
    public String toString()
    {
        return this.name;
    }
}
