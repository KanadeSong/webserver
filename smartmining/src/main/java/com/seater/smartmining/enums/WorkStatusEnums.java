package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/27 0027 15:01
 */
public enum WorkStatusEnums {

    UNKNOW("未知",0),
    ONSTART("开机",1),
    ONSTOP("停机",2);

    private String name;

    private Integer alias;

    WorkStatusEnums(String name, Integer alias){
        this.name = name;
        this.alias = alias;
    }

    public static WorkStatusEnums convert(Integer value){
        for(WorkStatusEnums status : WorkStatusEnums.values()){
            if(status.getAlias() == value){
                return status;
            }
        }
        return null;
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
