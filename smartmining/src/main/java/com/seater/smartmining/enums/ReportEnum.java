package com.seater.smartmining.enums;

/**
 * @Description 报表类型
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/24 11:28
 */
public enum ReportEnum {

    Unknown("Unknown", 0), // 未知
    CarDayReport("CarDayReport", 1), // 渣车日报
    MachineDayReport("MachineDayReport", 2), // 挖机日报
    CarMonthReport("CarMonthReport", 3), // 渣车月报
    MachineMonthReport("MachineMonthReport", 4), // 挖机月报
    CarClearing("CarClearing", 5), // 渣车成本结算表
    MachineClearing("MachineClearing", 6); // 挖机成本结算表

    private String name;
    private Integer value;

    ReportEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
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
