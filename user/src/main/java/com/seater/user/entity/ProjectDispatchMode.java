package com.seater.user.entity;

public enum ProjectDispatchMode
{
    Unknown("未知"),
    ExcavatorSchedule("挖机排班"),
    CompleteMixture ("完整混装"),
    GroupMixture("分组混装");
    private String value;
    ProjectDispatchMode(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
