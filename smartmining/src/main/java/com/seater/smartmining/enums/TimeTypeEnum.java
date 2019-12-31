package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 10:49
 */
public enum TimeTypeEnum {

    UNKNOW("未知", 0),
    DAY("天", 1),
    WEEK("周", 2),
    MONTH("月", 3),
    YEAR("年", 4),
    HISTORY("历史", 5);

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

    private String name;
    private Integer alias;

    TimeTypeEnum(String name,Integer alias){
        this.name = name;
        this.alias = alias;
    }
}
