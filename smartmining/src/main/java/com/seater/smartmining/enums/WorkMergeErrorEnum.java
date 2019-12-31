package com.seater.smartmining.enums;

/**
 * @Description:作业信息合并错误枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 14:56
 */
public enum WorkMergeErrorEnum {

    UNKNOW("未知", 0),
    SLAGCARUPDALOAD("渣车异常上传", 1),
    SLAGSITEUPDLOAD("渣场异常上传", 2);

    private String name;
    private Integer alias;

    WorkMergeErrorEnum(String name, Integer alias){
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }
}
