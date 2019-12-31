package com.seater.smartmining.enums;

/**
 * @Description:作业地点枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:31
 */
public enum PlaceEnum {

    UNKNOW("未知",0),
    WORKPLATFORM("工作平台",1),
    OUTZONE("区外作业",2);

    private String name;
    private Integer alians;

    PlaceEnum(String name, Integer alians){
        this.name = name;
        this.alians = alians;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlians() {
        return alians;
    }

    public void setAlians(Integer alians) {
        this.alians = alians;
    }
}
