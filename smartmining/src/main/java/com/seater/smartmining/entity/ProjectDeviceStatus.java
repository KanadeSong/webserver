package com.seater.smartmining.entity;

public enum ProjectDeviceStatus {

    Unknown("未知"),
    OffLine("离线"),
    OnLine("在线");

    private String value;

    ProjectDeviceStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
