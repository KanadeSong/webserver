package com.seater.smartmining.enums;

/**
 * @Description:任务调度执行枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/23 0023 9:55
 */
public enum  ScheduleEnum {

    Unknow("未知", 0),
    CarDayReport("渣车日表", 1),
    CarMonthReport("渣车月报", 2),
    DiggingDayReport("挖机日报", 3),
    DiggingMonthReport("挖机月报", 4),
    CostReport("成本分析", 5),
    DiggingWork("挖机上下机", 6),
    MatchingDegreeReport("效率统计", 7),
    DiggingPlaceReport("挖机工作平台统计", 8),
    DiggingMaterialReport("挖机物料统计", 9),
    SlagSiteReport("渣场统计", 10),
    CarWorkInfoValid("工作信息是否有效", 11),
    SlagCarWork("渣车上下机", 12),
    ExceptionReport("异常统计", 13),
    WorkInfoReport("作业合法统计", 14);


    private String name;
    private Integer value;

    ScheduleEnum(String name, Integer value){
        this.name = name;
        this.value = value;
    }
}
