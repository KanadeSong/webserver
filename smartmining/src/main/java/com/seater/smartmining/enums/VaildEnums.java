package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/14 0014 10:13
 */
public enum VaildEnums {

    VAILD("有效",0),
    NOTVAILDBYCAR("渣车无效",1),
    NOTVAILDBYDIGGING("挖机无效",2),
    BOTHNOTVALID("无效",3);

    VaildEnums(String name, Integer alias){
        this.name = name;
        this.alias = alias;
    }

    private String name;
    private Integer alias;

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
