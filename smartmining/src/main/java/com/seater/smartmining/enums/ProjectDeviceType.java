package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/18 0018 9:10
 */
public enum ProjectDeviceType {

    Unknown("未知", 0),
    SlagFieldDevice("渣场终端", 1),
    DiggingMachineDevice("挖机终端", 2),
    DetectionDevice("检测终端", 3),
    ScheduledDevice("调度终端", 4),
    SlagTruckDevice("渣车终端", 5),
    ForkliftDevice("铲车终端", 6),
    RollerDevice("压路机终端", 7),
    GunHammerDevice("炮锤终端", 8),
    SingleHookDevice("单勾终端", 9),
    WateringCarDevice("洒水车终端", 10),
    ScraperDevice("刮平机终端", 11),
    PunchDevice("穿孔机终端", 12);

    private String value;
    private Integer alian;

    ProjectDeviceType(String value, Integer alian) {
        this.value = value;
        this.alian = alian;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getAlian() {
        return alian;
    }

    public void setAlian(Integer alian) {
        this.alian = alian;
    }

}
