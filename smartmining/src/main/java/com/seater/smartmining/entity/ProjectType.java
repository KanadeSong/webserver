package com.seater.smartmining.entity;

public enum ProjectType
{
    Unknown("未知"),
    EconomicVersion("经济版"),
    UpgradeVersion("升级版"),
    EnhanceVersion("增强版"),
    CompleteVersion("完整版");
    private String value;
    private ProjectType(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
