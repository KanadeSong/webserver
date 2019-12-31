package com.seater.user.entity;

public enum RequiredPermission {
    Unknow("未知"),
    Require("必选"),
    UnRequire("非必选");

    private String value;

    private RequiredPermission(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
