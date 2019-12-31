package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 10:03
 */
public enum StopEnum {

    UNKNOW("未知", 0),
    EXAMINE("审核", 1),
    FORCE("强制", 2),
    AUTOMATIC("自动", 3);

    private String name;

    private Integer alias;

    StopEnum(String name, Integer alias){
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }
}
