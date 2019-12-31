package com.seater.smartmining.enums;

/**
 * @Description:设备操作类型
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/23 0023 15:46
 */
public enum DeviceDoStatusEnum {

    UnKnow(0, "未知"),
    Start(1, "强制上机"),
    Stop(2, "强制下机"),
    StartRequest(3, "上机申请"),
    StopRequest(4, "下机申请"),
    StartExamine(5, "上机审核"),
    StopExamine(6, "下机审核"),
    Pause(7, "暂停派车"),
    Recover(8, "恢复派车"),
    StopUse(9, "停用"),
    OnLine(10, "上机"),
    UnLine(11, "下机"),
    AutoUnLine(12, "自动下机"),
    Fault(13, "故障");

    private Integer value;
    private String name;

    DeviceDoStatusEnum(Integer value, String name){
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
