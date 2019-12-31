package com.seater.smartmining.entity;

public enum ProjectDispatchMode
{
    Unknown(0, "未知"),
    ExcavatorSchedule(1, "挖机排班"),
    CompleteMixture (2, "完整混装"),
    GroupMixture(3, "分组混装"),
    Auto(4,"智能调度"),
    AutoDistinguish(5, "自动识别");

    private Integer alians;
    private String value;

    ProjectDispatchMode(Integer alians, String value) {
        this.alians = alians;
        this.value = value;
    }

    public static ProjectDispatchMode converMode(Integer alians){
        for(ProjectDispatchMode mode : ProjectDispatchMode.values()){
            if(mode.alians == alians){
                return mode;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public Integer getAlians() {
        return alians;
    }

    public void setAlians(Integer alians) {
        this.alians = alians;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
