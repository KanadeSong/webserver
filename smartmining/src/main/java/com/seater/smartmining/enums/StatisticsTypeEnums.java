package com.seater.smartmining.enums;

import lombok.Data;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/20 0020 16:37
 */
public enum StatisticsTypeEnums {
    UNKNOW("未知",0),
    DAYCOUNT("当日小计",1),
    MONTHCOUNT("当月小计",2),
    HISTORYCOUNT("历史累计",3);

    private String name;

    private Integer value;

    private StatisticsTypeEnums(String name,Integer value){
        this.name = name;
        this.value = value;
    }

    //获取名称
    public static String getName(Integer value){
        for(StatisticsTypeEnums statistics : StatisticsTypeEnums.values()){
            if(statistics.getValue() == value){
                return statistics.name;
            }
        }
        return null;
    }

    public static StatisticsTypeEnums convert(Integer value){
        for(StatisticsTypeEnums statistics : StatisticsTypeEnums.values()){
            if(statistics.getValue() == value){
                return statistics;
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
