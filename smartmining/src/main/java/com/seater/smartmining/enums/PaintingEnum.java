package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 9:33
 */
public enum PaintingEnum {

    Unknow("未知",0),
    SlagSite("渣场",1),
    Place("工作平台",2),
    ElseLiving("其它", 3);

    private String name;
    private Integer value;
    PaintingEnum(String name,Integer value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.name;
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
