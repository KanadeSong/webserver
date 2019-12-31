package com.seater.smartmining.enums;

/**
 * @Description 组类型
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 10:30
 */
public enum GroupType {

    Unknown("未知"),
    Captain("队长"),
    /**
     * 入范围进组,出范围出组
     */
    SlagSite("渣场"),
    UndistributedCar("未分配渣车组"),
    UndistributedMachine("未分配挖机组"),
    Manage("管理组"),
    /**
     * 地图上框选组
     */
    Temp("临时组"),
    Support("辅助设备组");

    private String value;

    GroupType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
