package com.seater.user.entity;

public enum ProjectWorkTimePoint {
    Today("当天"),
    Tomorrow("明天");

    private String value;
    ProjectWorkTimePoint(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
