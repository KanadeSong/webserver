package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/9 0009 16:30
 */
public enum DeviceStartStatusEnum {

    UnKnow(0, "未知"),
    DiggingMachine(1, "挖机终端启用"),
    Check(2, "检测终端启用"),
    All(3, "挖机终端和检测终端都启用"),
    Only(4, "单渣场，仅仅有渣场终端");

    private Integer value;
    private String name;

    DeviceStartStatusEnum(Integer value, String name){
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

}
