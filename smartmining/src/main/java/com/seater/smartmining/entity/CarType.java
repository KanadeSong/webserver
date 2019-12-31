package com.seater.smartmining.entity;

public enum CarType
{
    Unknow("未知类型", 0),
    OilCar("加油车", 1),
    DiggingMachine("挖机", 2),
    SlagCar("渣车", 3),
    Forklift("铲车", 4),
    Roller("压路机", 5),
    GunHammer("炮锤", 6),
    SingleHook("单勾", 7),
    WateringCar("洒水车", 8),
    Scraper("刮平车", 9),
    Punch("穿孔机", 10);

    private String name;
    private Integer value;
    CarType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
    @Override
    public String toString()
    {
        return this.name;
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

}
