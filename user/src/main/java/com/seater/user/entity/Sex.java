package com.seater.user.entity;

public enum Sex
{
    Unknow("未知"),
    Male("男"),
    Female("女");

    private String value;
    private Sex(String value)
    {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
