package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/29 0029 15:27
 */
public enum  PricingTypeEnums {
    Unknow("未知",0),
    Hour("计时",1),
    Cube("计方",2),
    HourAndCube("计时计方",3);

    private String name;
    private Integer value;
    PricingTypeEnums(String name,Integer value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public static PricingTypeEnums convert(Integer value){
        for(PricingTypeEnums pricing : PricingTypeEnums.values()){
            if(pricing.getValue() == value){
                return pricing;
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
