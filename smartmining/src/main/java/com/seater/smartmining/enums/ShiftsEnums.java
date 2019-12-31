package com.seater.smartmining.enums;

/**
 * @Description:黑白班枚举类
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 14:24
 */
public enum ShiftsEnums {

    UNKNOW("未知",0),
    DAYSHIFT("日",1),
    BLACKSHIFT("夜",2);

    private String name;
    private Integer alias;

    private ShiftsEnums(String name,Integer alias){
        this.name = name;
        this.alias = alias;
    }

    public static ShiftsEnums converShift(Integer value){
        for(ShiftsEnums shifts : ShiftsEnums.values()){
            if(shifts.getAlias() == value){
                return shifts;
            }
        }
        return null;
    }

    //获取名称
    public static Integer getValue(ShiftsEnums shiftsEnums){
        return shiftsEnums.getAlias();
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
