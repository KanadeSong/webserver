package com.seater.smartmining.entity;

public enum ProjectCarWorkStatus {

    Unknown("未知",0),
    NoLoad("未装载", 1),
    UnCheck("未检测",2),
    UnUnload("未卸载",3),
    WaitLoadUp("等待挖机上传",4),
    WaitCheckUp("等待检测站上传",5),
    WaitLoadCheckUp("等待挖机和检测站上传",6),
    Finish("完成",7);

    private String value;
    private Integer alias;

    //获取对应的对象
    public static ProjectCarWorkStatus getName(Integer alias){
        for(ProjectCarWorkStatus statistics : ProjectCarWorkStatus.values()){
            if(statistics.getAlias() == alias){
                return statistics;
            }
        }
        return null;
    }

    ProjectCarWorkStatus(String value,Integer alias) {
        this.value = value;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
