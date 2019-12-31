package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/21 0021 15:27
 */
public enum WorkInfoStatusEnums {

    UNKNOW("未知",0),
    AUDITEDONSTART("开机待审核",1),
    AUDITEDSTART("开机审核成功",2),
    AUDITEDONSTOP("关机待审核",3),
    AUDITEDSTOP("关机审核成功",4);

    private String name;
    private Integer value;

    WorkInfoStatusEnums(String name, Integer value){
        this.name = name;
        this.value = value;
    }

    public static WorkInfoStatusEnums convert(Integer value){
        for(WorkInfoStatusEnums status : WorkInfoStatusEnums.values()){
            if(status.getValue() == value){
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
