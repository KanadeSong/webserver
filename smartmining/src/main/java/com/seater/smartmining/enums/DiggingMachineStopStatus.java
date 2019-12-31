package com.seater.smartmining.enums;


public enum DiggingMachineStopStatus {

    Normal("正常"),
    Fault("故障"),
    STOP("停用"),
    PAUSE("暂停");

    private String value;
    DiggingMachineStopStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
