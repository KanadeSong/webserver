package com.seater.smartmining.enums;

/**
 * @Description:登录类型
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/22 0022 17:18
 */
public enum LoginEnums {

    Unknow("未知",0),
    APP("APP端",1),
    WEB("Web端",2);

    private String name;
    private Integer value;
    LoginEnums(String name,Integer value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.name;
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
}
