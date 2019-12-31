package com.seater.smartmining.enums;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/31 0031 11:28
 */
public enum ProjectCarStatus {

    Unknow(0, "未知"),
    Working(1, "上机"),
    Stop(2, "下机"),
    Fault(3, "故障"),
    StopWork(4,"停用");

    private String name;

    private Integer value;
    ProjectCarStatus(Integer value ,String name)
    {
        this.value = value;
        this.name = name;
    }

    public static ProjectCarStatus converStatus(Integer value){
        for(ProjectCarStatus status : ProjectCarStatus.values()){
            if(status.getValue() == value){
                return status;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    @Override
    public String toString()
    {
        return this.name;
    }
}
