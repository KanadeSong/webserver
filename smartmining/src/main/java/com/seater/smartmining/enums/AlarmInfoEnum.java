package com.seater.smartmining.enums;

/**
 * @Description:  设备警告信息枚举类   //todo 具体定义待定
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/18 0018 14:21
 */
public enum AlarmInfoEnum {

    Unknow("未知",0);

    private String name;
    private Integer value;

    AlarmInfoEnum(String name, Integer value){
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
