package com.seater.smartmining.enums;

/**
 * @Description:工作地点状态枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:35
 */
public enum PlaceStatusEnum {

    UNKNOW("未知", 0),
    START("启用",1),
    STOP("停用",2);

    private String name;
    private Integer alians;

    PlaceStatusEnum(String name, Integer alians){
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
