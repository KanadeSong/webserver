package com.seater.smartmining.enums;

/**
 * @Description:设备异常信息枚举类   //todo 具体定义待定
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/18 0018 14:22
 */
public enum FaultInfoEnum {

    Unknow("未知",0);

    private String name;
    private Integer value;

    FaultInfoEnum(String name, Integer value){
        this.name = name;
        this.value = value;
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
