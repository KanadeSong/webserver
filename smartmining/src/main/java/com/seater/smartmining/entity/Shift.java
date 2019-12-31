package com.seater.smartmining.entity;

public enum Shift {
    Unknown("未知", 0),
    Early("早班", 1),
    Night("晚班", 2);

    private String value;

    private Integer alias;

    Shift(String value, Integer alias) {
        this.value = value;
        this.alias = alias;
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

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }
}
