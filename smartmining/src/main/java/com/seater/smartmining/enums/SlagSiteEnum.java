package com.seater.smartmining.enums;

/**
 * @Description:渣场枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/23 0023 16:17
 */
public enum SlagSiteEnum {
    UNKNOW("未知",0),
    INNERROW("内排",1),
    ROADREPAIR("修路",2),
    TEMPORARY("临时",3),
    EFFLUX("外排",4),
    COALYARD("煤场",5);

    private String name;
    private Integer alias;

    private SlagSiteEnum(String name, Integer alias){
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
