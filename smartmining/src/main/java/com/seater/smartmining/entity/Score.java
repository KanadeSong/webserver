package com.seater.smartmining.entity;

public enum Score {

    Unknown("未知", 0),
    Pass("及格", 1),
    UnPass("不及格", 2);

    private String value;
    private Integer alias;

    Score(String value, Integer alias) {
        this.value = value;
        this.alias = alias;
    }

    //获取对应的对象
    public static Score getName(Integer alias){
        for(Score statistics : Score.values()){
            if(statistics.getAlias() == alias){
                return statistics;
            }
        }
        return null;
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
